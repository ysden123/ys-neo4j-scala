/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import com.stulsoft.neo4js.session.SessionManager
import com.typesafe.scalalogging.StrictLogging
import org.neo4j.driver.async.ResultCursor
import org.neo4j.driver.summary.ResultSummary

import scala.concurrent.{Future, Promise}
import java.util.concurrent.{CompletableFuture, CompletionStage}
import scala.util.{Failure, Success}

object AsyncEx1 extends StrictLogging:

  private def printAllPersons(): Unit =
    logger.info("==>printAllPersons")
    val query = "MATCH (p:Person) RETURN (p)"
    val asyncSession = SessionManager.asyncSession()
    asyncSession
      .executeReadAsync(tx => tx.runAsync(query)
        .thenCompose(cursor => cursor.forEachAsync(record => {
          val person = Person.fromRecord(record, "p")
          logger.info("(1) {}", person)
        })))
    logger.info("<==printAllPersons")

  private def handleAllPersons(f: org.neo4j.driver.Record => Unit): Unit =
    logger.info("==>handleAllPersons")
    val query = "MATCH (p:Person) RETURN (p)"
    val asyncSession = SessionManager.asyncSession()
    asyncSession
      .executeReadAsync(tx => tx.runAsync(query)
        .thenCompose(cursor => cursor.forEachAsync(record => {
          f(record)
        })))
    logger.info("<==handleAllPersons")

  private def handleAllPersons2(query:String, valueName:String, f: (org.neo4j.driver.Record, String) => Unit): Unit =
    logger.info("==>handleAllPersons2")
    val asyncSession = SessionManager.asyncSession()
    asyncSession
      .executeReadAsync(tx => tx.runAsync(query)
        .thenCompose(cursor => cursor.forEachAsync(record => {
          f(record, valueName)
        })))
    logger.info("<==handleAllPersons2")

  private def printPerson(record: org.neo4j.driver.Record): Unit =
    val person = Person.fromRecord(record, "p")
    logger.info("(2) {}", person)

  private def printPerson2(record: org.neo4j.driver.Record, valueName:String): Unit =
    val person = Person.fromRecord(record, valueName)
    logger.info("(3) {}", person)

  def main(args: Array[String]): Unit =
    logger.info("==>main")

    printAllPersons()
    handleAllPersons(printPerson)
    handleAllPersons2("MATCH (p:Person) RETURN (p)", "p", printPerson2)
    handleAllPersons2("MATCH (p:Person {sex:'male'}) RETURN (p)", "p", printPerson2)

    logger.info("Waiting...")
    Thread.sleep(1_000)
    logger.info("Completing...")