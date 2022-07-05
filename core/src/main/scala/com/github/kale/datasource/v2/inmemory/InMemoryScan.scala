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

package com.github.kale.datasource.v2.inmemory

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReader, PartitionReaderFactory, Scan, ScanBuilder}
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}

import java.util.concurrent.atomic.AtomicInteger

class InMemoryScanBuilder extends ScanBuilder {
  override def build(): Scan = {
    new InMemoryScan
  }
}

class InMemoryScan extends Scan with Batch {
  override def readSchema(): StructType = {
    StructType.apply(List(StructField("id", IntegerType)))
  }

  override def planInputPartitions(): Array[InputPartition] = {
    throw new UnsupportedOperationException("InMemoryScan . ")
  }

  override def createReaderFactory(): PartitionReaderFactory = new ReaderFactory
}

class ReaderFactory extends PartitionReaderFactory {
  override def createReader(partition: InputPartition): PartitionReader[InternalRow] = {
    new Reader
  }
}

class Reader extends PartitionReader[InternalRow] with Logging {

  private val lines = new AtomicInteger(0)

  override def next(): Boolean = {
    val i = lines.getAndIncrement()
    if (i < 10) {
      true
    } else {
      false
    }
  }

  override def get(): InternalRow = {
    val row = new GenericInternalRow(1)
    row.update(0, lines.get())
    row
  }

  override def close(): Unit = {
    logInfo(s"closing ${lines.get()}")
  }
}
