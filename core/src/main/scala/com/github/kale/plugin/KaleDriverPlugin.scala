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

package com.github.kale.plugin

import org.apache.spark.SparkContext
import org.apache.spark.api.plugin.{DriverPlugin, PluginContext}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.ui.KaleUI

import java.util
import java.util.concurrent.atomic.AtomicInteger
import java.util.{Timer, TimerTask, UUID}

case class KaleDriverPlugin() extends DriverPlugin with Logging {

  @transient val timer = new Timer()

  private val times = new AtomicInteger(0)

  override def init(sc: SparkContext, pluginContext: PluginContext): util.Map[String, String] = {
    KaleUI.attachUI(sc)
    sendKaleMessage(pluginContext)
    super.init(sc, pluginContext)
  }

  override def shutdown(): Unit = {
    timer.cancel()
  }

  override def receive(message: Any): AnyRef = {
    logInfo(s"Receive message: $message")
    Unit
  }

  private def sendKaleMessage(pluginContext: PluginContext): Unit = {
    timer.scheduleAtFixedRate(new TimerTask {
      override def run(): Unit = {
        val i = times.incrementAndGet()
        if (i <= 10) pluginContext.send(s"Kale message: ${UUID.randomUUID()}")
      }
    }, 0, 5000)
  }

}
