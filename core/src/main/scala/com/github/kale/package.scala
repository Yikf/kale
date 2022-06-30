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

package com.github

import com.github.kale.errors.KaleErrors.{cantFindBuildInfo, errorLoadBuildProperties}
import org.apache.spark.sql.SparkSessionExtensions

import java.util.Properties

package object kale {

  type Extension = SparkSessionExtensions => Unit

  private object BuildInfo {
    val (kale_version: String, user: String) = {
      val resourceStream = Thread.currentThread().getContextClassLoader
        .getResourceAsStream("kale-version-info.properties")

      if (resourceStream == null) {
        cantFindBuildInfo()
      }

      val unknownProp = "<unknown>"
      try {
        val props = new Properties()
        props.load(resourceStream)

        (props.getProperty("version", unknownProp),
          props.getProperty("user", unknownProp))
      } catch {
        case e: Exception =>
          errorLoadBuildProperties(e)
      } finally {
        if (resourceStream != null) {
          try {
            resourceStream.close()
          } catch {
            case e: Exception =>
              errorLoadBuildProperties(e)
          }
        }
      }
    }
  }

  val KALE_VERSION: String = BuildInfo.kale_version

  val CURRENT_USER: String = BuildInfo.user
}
