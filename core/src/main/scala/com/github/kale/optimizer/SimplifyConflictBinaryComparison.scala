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

import org.apache.spark.sql.catalyst.expressions.Literal.FalseLiteral
import org.apache.spark.sql.catalyst.expressions.{And, Attribute, BinaryComparison, EqualTo, Expression, GreaterThan, GreaterThanOrEqual, LessThan, LessThanOrEqual, Literal, PredicateHelper}
import org.apache.spark.sql.catalyst.plans.logical.{ConstraintHelper, LogicalPlan}
import org.apache.spark.sql.catalyst.rules.Rule
import org.apache.spark.sql.catalyst.trees.TreePattern.BINARY_COMPARISON
import org.apache.spark.sql.errors.QueryCompilationErrors

/**
 * Binary comparison operators for simplifying conflicts.
 */
case class SimplifyConflictBinaryComparison()
  extends Rule[LogicalPlan] with PredicateHelper with ConstraintHelper {

  private def normalBinaryComparison(
      b: BinaryComparison): BinaryComparison = {
    b match {
      case e @BinaryComparison(_: Literal, _: Attribute) => flipExpression(e)
      case _ => b
    }
  }

  // todo implement flip to normal binary comparison expression (i.e. 1 < a => a > 3)
  private def flipExpression(expression: BinaryComparison): BinaryComparison = {
    expression
  }

  private def binaryFoldable(l: Expression, r: Expression): Boolean = {
    l.foldable && r.foldable
  }

  implicit def anyToBoolean(value: Any): Boolean = {
    value match {
      case a: Boolean => a
      case _ =>
        throw QueryCompilationErrors.invalidFieldTypeForCorruptRecordError()
    }
  }

  val applyConflictTemplate: PartialFunction[(Expression, Expression), Expression] = {
    case (BinaryComparison(_, v1), BinaryComparison(_, v2))
      if EqualTo(v1, v2).eval() == null => FalseLiteral

    case (EqualTo(_, v1), EqualTo(_, v2)) if !EqualTo(v1, v2).eval() => FalseLiteral
    case (e1 @ EqualTo(_, v1), GreaterThan(_, v2)) =>
      if (GreaterThan(v1, v2).eval()) {
        e1
      } else {
        FalseLiteral
      }
    case (e1 @ EqualTo(_, v1), GreaterThanOrEqual(_, v2)) =>
      if (GreaterThanOrEqual(v1, v2).eval()) {
        e1
      } else {
        FalseLiteral
      }
    case (e1 @ EqualTo(_, v1), LessThan(_, v2)) =>
      if (LessThan(v1, v2).eval()) {
        e1
      } else {
        FalseLiteral
      }
    case (e1 @ EqualTo(_, v1), LessThanOrEqual(_, v2)) =>
      if (LessThanOrEqual(v1, v2).eval()) {
        e1
      } else {
        FalseLiteral
      }

    case (e1 @ GreaterThan(_, v1), e2 @ GreaterThan(_, v2)) =>
      if (GreaterThan(v1, v2).eval()) {
        e1
      } else {
        e2
      }
    case (GreaterThan(_, v1), LessThan(_, v2))
      if GreaterThanOrEqual(v1, v2).eval() => FalseLiteral
    case (e1 @ GreaterThan(_, v1), e2 @ GreaterThanOrEqual(_, v2)) =>
      if (GreaterThanOrEqual(v1, v2).eval()) {
        e1
      } else {
        e2
      }
    case (GreaterThan(_, v1), LessThanOrEqual(_, v2))
      if GreaterThanOrEqual(v1, v2).eval() => FalseLiteral

    case (e1 @ LessThan(_, v1), e2 @ LessThan(_, v2)) =>
      if (LessThan(v1, v2).eval()) {
        e1
      } else {
        e2
      }
    case (LessThan(_, v1), GreaterThanOrEqual(_, v2))
      if LessThanOrEqual(v1, v2).eval() => FalseLiteral
    case (e1 @ LessThan(_, v1), e2 @ LessThanOrEqual(_, v2)) =>
      if (LessThanOrEqual(v1, v2).eval()) {
        e1
      } else {
        e2
      }

    case (e1 @ GreaterThanOrEqual(_, v1), e2 @ GreaterThanOrEqual(_, v2)) =>
      if (GreaterThan(v1, v2).eval()) {
        e1
      } else {
        e2
      }
    case (e1 @ GreaterThanOrEqual(attr, v1), e2 @ LessThanOrEqual(_, v2)) =>
      if (GreaterThan(v1, v2).eval()) {
        FalseLiteral
      } else if (LessThan(v1, v2).eval()) {
        And(e1, e2)
      } else {
        EqualTo(attr, v1)
      }
  }

  private val applySimplifyConflict: PartialFunction[Expression, Expression] = {
    case and @ And(left: BinaryComparison, right: BinaryComparison) =>
      val (l, r) = (normalBinaryComparison(left), normalBinaryComparison(right))

      if (l.left.semanticEquals(r.left) && binaryFoldable(l.right, r.right)) {
        if (applyConflictTemplate.isDefinedAt(l, r)) {
          applyConflictTemplate((l, r))
        } else if (applyConflictTemplate.isDefinedAt((r, l))) {
          applyConflictTemplate((r, l))
        } else {
          and
        }
      } else {
        and
      }
  }

  def apply(plan: LogicalPlan): LogicalPlan = plan.transformWithPruning(
    _.containsPattern(BINARY_COMPARISON), ruleId) {
    case l: LogicalPlan =>
      l.transformExpressionsUpWithPruning(_.containsPattern(BINARY_COMPARISON)) {
        applySimplifyConflict
      }
  }
}
