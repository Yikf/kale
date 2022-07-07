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

package com.github.kale.common

// TODO This is a test
object StringBeautiful {

  implicit class Inner(val sc: StringContext) {
    sc.parts.foreach(println)
    println("string length:" + sc.parts.size)

    def beautiful(v: Any*): String = {
      v.foreach(println)
      println("length: " + v.size)
      "a"
    }
  }
}

object Driver {
  def main(args: Array[String]): Unit = {
    import StringBeautiful._

    val a = "2"
    val value = beautiful"""$a ha $a aa"""

    println(KaleInfo("func"))
  }
}

case class KaleInfo(f: String) {
  override def toString: String = {
    f
  }
}

class A extends Product2[String, String] {

  override def productElement(n: Int): Any = ???

  override def productArity: Int = ???

  override def canEqual(that: Any): Boolean = ???

  override def _1: String = ???

  override def _2: String = ???
}
