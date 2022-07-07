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

import com.github.kale.utils.ExpressionUtils
import org.apache.spark.sql.catalyst.{FunctionIdentifier, InternalRow}
import org.apache.spark.sql.catalyst.expressions.{Expression, ExpressionDescription, UnaryExpression}
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.types.{DataType, StringType}
import org.apache.spark.unsafe.types.UTF8String

import java.net.URLEncoder

// scalastyle:off line.size.limit
@ExpressionDescription(
  usage = """_FUNC_() - Returns the Kale version. The string contains 2 fields, the first being a release version and the second being a git revision.""",
  examples = """
    Examples:
      > SELECT _FUNC_();
       3.1.0 a6d6ea3efedbad14d99c24143834cd4e2e52fb40
  """,
  since = "3.0.0",
  group = "extension_func",
  source = "kale_version")
// scalastyle:on line.size.limit
case class UrlEncode(child: Expression) extends UnaryExpression with CodegenFallback {

  override def nullable: Boolean = child.nullable

  override def eval(input: InternalRow): Any = {
    val string = child.eval(input).asInstanceOf[UTF8String]
    UTF8String.fromString(URLEncoder.encode(string.toString, "utf-8"))
  }

  override def dataType: DataType = StringType

  override def prettyName: String = "url_encode"

  override protected def withNewChildInternal(newChild: Expression): Expression = this.copy(child = newChild)
}

object UrlEncode {
  val functionDescribe = (
    new FunctionIdentifier("url_encode"),
    ExpressionUtils.buildExpressionInfo(classOf[UrlEncode]),
    (c: Seq[Expression]) => UrlEncode(c.head)
  )
}
