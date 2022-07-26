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

package com.github.kale

import com.github.kale.expression.{KalePrefixExpression, KaleVersion, UrlDecode, UrlEncode}
import com.github.kale.optimizer.{RepartitionSmallFile, SimplifyConflictBinaryComparison}
import com.github.kale.parser.KaleParser
import org.apache.spark.sql.SparkSessionExtensions

class KaleSparkExtension extends Extension {

  override def apply(extension: SparkSessionExtensions): Unit = {
    extension.injectFunction(KaleVersion.functionDescribe)
    extension.injectFunction(KalePrefixExpression.kalePrefix)
    extension.injectFunction(UrlEncode.functionDescribe)
    extension.injectFunction(UrlDecode.functionDescribe)

    extension.injectParser((_, parser) => KaleParser(parser))

    extension.injectOptimizerRule(_ => RepartitionSmallFile())
    extension.injectOptimizerRule(_ => SimplifyConflictBinaryComparison())
  }
}
