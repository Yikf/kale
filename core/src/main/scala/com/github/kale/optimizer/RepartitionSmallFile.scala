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

package com.github.kale.optimizer

import com.github.kale.common.KaleConf
import com.github.kale.optimizer.RepartitionSmallFile.repartitionSmallFile_tag
import org.apache.spark.sql.catalyst.plans.logical.{LogicalPlan, Repartition}
import org.apache.spark.sql.catalyst.rules.Rule
import org.apache.spark.sql.catalyst.trees.TreeNodeTag

case class RepartitionSmallFile() extends Rule[LogicalPlan] {
  override def apply(plan: LogicalPlan): LogicalPlan = {

    if (!conf.getConf(KaleConf.KALE_EXTENSION_ENABLE)) {
      return plan
    }

    plan match {
      case r: Repartition if r.getTagValue(repartitionSmallFile_tag).get.equals("true") => r
      case plan =>
        val planWithRepartition = Repartition(1, shuffle = true, plan)
        planWithRepartition.setTagValue(repartitionSmallFile_tag, "true")
        planWithRepartition

    }
  }
}

object RepartitionSmallFile {
  final val repartitionSmallFile_tag = TreeNodeTag[String]("repartitionSmallFile")
}
