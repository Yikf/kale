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

package org.apache.spark.benchmark

import org.apache.commons.codec.net.URLCodec

import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets

object UrlCodecBenchmark  extends BenchmarkBase {

  private val url: String = "https://spark.apache.org/"
  private val encodedUrl: String = "https%3A%2F%2Fspark.apache.org"

  private val valuesPerIteration: Int = 10 * 1000
  private val enc: String = StandardCharsets.UTF_8.name()

  private val codec = new URLCodec(enc)

  def runEncodeBenchmark(benchmark: Benchmark): Unit = {
    benchmark.addCase("Java url encode") {  _ =>
      for (_ <- 0 until valuesPerIteration) {
        URLEncoder.encode(url, enc)
      }
    }

    val codec = new URLCodec(enc)
    benchmark.addCase("Guava url encode") { _ =>
      for (_ <- 0 until valuesPerIteration) {
        codec.encode(url, enc)
      }
    }
  }

  def runDecodeBenchmark(benchmark: Benchmark): Unit = {
    benchmark.addCase("Java url decode") {  _ =>
      for (_ <- 0 until valuesPerIteration) {
        URLDecoder.decode(encodedUrl, enc)
      }
    }

    benchmark.addCase("Guava url decode") { _ =>
      for (_ <- 0 until valuesPerIteration) {
        codec.decode(encodedUrl, enc)
      }
    }
  }

  def requireSameSemantics(): (Boolean, Boolean)  = {
    (URLEncoder.encode(url, enc) == codec.encode(url, enc),
      URLDecoder.decode(encodedUrl, enc) == codec.decode(encodedUrl, enc))
  }

  override def runBenchmarkSuite(mainArgs: Array[String]): Unit = {
    val benchmark = new Benchmark("Url codec benchmark",
      valuesPerIteration,
      minNumIters = 10,
      output = output)


    runEncodeBenchmark(benchmark)
    runDecodeBenchmark(benchmark)

    assert(requireSameSemantics()._1)
    assert(requireSameSemantics()._2)

    benchmark.run()
  }
}
