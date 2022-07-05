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

package com.github.kale.errors

object KaleErrors {

  def cantFindBuildInfo(): Unit = {
    throw KaleException("Could not find kale-version-info.properties")
  }

  def errorLoadBuildProperties(e: Throwable): Unit = {
    throw new KaleException("Error closing kale build info resource stream", e)
  }

  def kaleNotSupportOperator(op: String): Exception = {
    new UnsupportedOperationException(s"Kale not support operator [$op]")
  }

}
