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

package com.github.kale.expression

import com.github.kale.KaleSuiteBase
import org.apache.spark.sql.Row

// TODO Stripping out the spark
class KaleFunctionExpressionSuite extends KaleSuiteBase {

  test("kaleVersion expression") {
    val version = KaleVersion().eval()
    assert(version.toString == "1.0-SNAPSHOT")
  }

  test("Url decode & encode") {
    val url = "https://github.com/Yikf"
    val encodeUrl = "https%3A%2F%2Fgithub.com%2FYikf"

    checkAnswer(Row(encodeUrl)){ _ =>
      spark.sql(s"select url_encode('$url')").collect().head
    }

    checkAnswer(Row(url)){ _ =>
      spark.sql(s"select url_decode('$encodeUrl')").collect().head
    }
  }
}
