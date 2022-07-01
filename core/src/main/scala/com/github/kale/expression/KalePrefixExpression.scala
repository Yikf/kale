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
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.catalyst.expressions.{Expression, ExpressionDescription, UnaryExpression}
import org.apache.spark.sql.types.{DataType, StringType}
import org.apache.spark.unsafe.types.UTF8String

// scalastyle:off line.size.limit
@ExpressionDescription(
  usage = """_FUNC_() - return string with kale prefix.""",
  examples = """
    Examples:
      > SELECT _FUNC_(col);
       kale-col
  """,
  since = "3.0.0",
  group = "extension_func",
  source = "kale_prefix")
// scalastyle:on line.size.limit
case class KalePrefixExpression(child: Expression) extends UnaryExpression
    with CodegenFallback {

  override def dataType: DataType = StringType

  override protected def withNewChildInternal(newChild: Expression): Expression = this.copy(child = newChild)

  override def eval(input: InternalRow): Any = {
    val srcString = child.eval(input).asInstanceOf[UTF8String]
    UTF8String.fromString(s"kale-$srcString")
  }

  override def prettyName: String = "kale-prefix"
}

object KalePrefixExpression {
  val kalePrefix = (
    FunctionIdentifier("kale_prefix"),
    ExpressionUtils.buildExpressionInfo[KalePrefixExpression](classOf[KalePrefixExpression]),
    (c: Seq[Expression]) => KalePrefixExpression(c.head)
  )
}
