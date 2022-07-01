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

import com.github.kale.{KaleSparkExtension, KaleSuiteBase}
import org.apache.spark.sql.catalyst.plans.physical.SinglePartition
import org.apache.spark.sql.execution.exchange.Exchange

class RepartitionSmallFiles extends KaleSuiteBase {

  test("Repartition small files ") {
    withSQLConf(
      ("spark.sql.extensions", classOf[KaleSparkExtension].getCanonicalName),
      ("spark.sql.adaptive.enabled", "false")) {
      val frame = spark.sql("select * from values('1'),('2')")

      val plan = frame.queryExecution.executedPlan
      assert(plan.isInstanceOf[Exchange])
      assert(plan.outputPartitioning == SinglePartition)
      assert(plan.execute().partitions.length == 1)

    }
  }
}
