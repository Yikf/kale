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

import com.github.kale.KaleSparkExtension
import org.apache.spark.sql.SparkSession
import org.scalatest.funsuite.AnyFunSuite

// TODO Stripping out the spark
class KaleVersionSuite extends AnyFunSuite {
  test("kaleVersion expression") {
    val version = KaleVersion().eval()
    assert(version.toString == "1.0-SNAPSHOT")
  }

  test("KaleVersion with spark sql extension") {
    val session = SparkSession.builder()
      .withExtensions(new KaleSparkExtension)
      .master("local")
      .appName("kale_version")
      .getOrCreate()
    session.sql("select kale_version() as a").show()
  }

  test("KaleVersion with spark sql extension22") {
    val session = SparkSession.builder()
      .withExtensions(new KaleSparkExtension)
      .master("local")
      .appName("kale_version")
      .getOrCreate()
    session.sql("select kale_prefix(col1) from values('1'),('2'),('3')").show()
  }
}
