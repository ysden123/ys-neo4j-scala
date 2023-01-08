/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import org.neo4j.driver.{Value, Record}

case class Person(name: String, sex: String):
  override def toString: String =
    s"Person {name: '$name', sex: '$sex'}"

object Person extends Neo4jDataObject[Person]:
  override def fromValue(aValue: Value): Person =
    Person(aValue.get("name").asString(), aValue.get("sex").asString())

  override def fromRecord(record: Record, valueName:String): Person =
    fromValue(record.get(valueName))