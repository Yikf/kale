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

package com.github.kale.datasource.v2.kale

import com.github.kale.errors.KaleErrors.kaleNotSupportOperator
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.catalog.{Identifier, Table, TableCatalog, TableChange}
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util
import scala.collection.mutable

class KaleCatalog extends TableCatalog with Logging {

  @transient private var catalog_name: String = _
  @transient private var options: CaseInsensitiveStringMap = _

  private val cacheTable = mutable.Map.empty[Identifier, Table]

  override def initialize(name: String, options: CaseInsensitiveStringMap): Unit = {
    this.catalog_name = name
    this.options = options
  }

  override def name(): String = "kale-catalog"

  override def listTables(namespace: Array[String]): Array[Identifier] = {
    cacheTable.keysIterator.toArray
  }

  override def loadTable(ident: Identifier): Table = {
    cacheTable(ident)
  }

  override def createTable(ident: Identifier,
      schema: StructType, partitions: Array[Transform], properties: util.Map[String, String]): Table = {
    // TODO convert to Table
    cacheTable += (ident -> null)
    null
  }

  override def alterTable(ident: Identifier, changes: TableChange*): Table = {
    throw kaleNotSupportOperator("Alter table")
  }

  override def dropTable(ident: Identifier): Boolean = {
    throw kaleNotSupportOperator("Drop table")
  }

  override def renameTable(oldIdent: Identifier, newIdent: Identifier): Unit = {
    throw kaleNotSupportOperator("Rename table")
  }
}
