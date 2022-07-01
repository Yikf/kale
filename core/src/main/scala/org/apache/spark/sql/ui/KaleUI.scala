/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.ui

import org.apache.spark.SparkContext
import org.apache.spark.sql.execution.ui.{SQLAppStatusListener, SQLAppStatusStore}
import org.apache.spark.status.ElementTrackingStore

object KaleUI {

  def attachUI(sc: SparkContext): Unit = {
    // 1. kv 存储
    // 2. 监听事件接收到信息，将信息存储到kv
    // 3. kv 作为参数传递给table 、 page
    // 4. 刷新页面，page 从 kv 中获取信息进行渲染
    val kvStore = sc.statusStore.store.asInstanceOf[ElementTrackingStore]

    val listener = new SQLAppStatusListener(sc.conf, kvStore, live = true)
    sc.listenerBus.addToStatusQueue(listener)
    val statusStore = new SQLAppStatusStore(kvStore, Some(listener))

    sc.ui.foreach(new KaleLabel(statusStore, _))
  }
}
