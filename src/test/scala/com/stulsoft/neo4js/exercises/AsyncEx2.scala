/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import com.stulsoft.neo4js.session.SessionManager
import com.typesafe.scalalogging.StrictLogging

import java.util.concurrent.{CompletableFuture, CompletionStage}
import scala.collection.mutable.ListBuffer
import scala.runtime.Nothing$

object AsyncEx2 extends StrictLogging:

  private def findOnePerson(name: String): CompletionStage[Person] =
    logger.info("==>findOnePerson")
    val result = CompletableFuture[Person]
    val query = s"MATCH (p:Person {name:'$name'}) RETURN (p)"
    SessionManager.asyncSession()
      .executeReadAsync(tx => tx.runAsync(query)
        .thenCompose(cursor => cursor.peekAsync().thenCompose(record => {
          val person = Person.fromRecord(record, "p")
          result.complete(person)
          result
        })))
    logger.info("<==findOnePerson")
    result

  private def findAllPersons(): CompletionStage[ListBuffer[Person]] =
    logger.info("==>findAllPersons")
    val result = CompletableFuture[ListBuffer[Person]]
    val query = s"MATCH (p:Person) RETURN (p)"
    val persons = ListBuffer[Person]()
    SessionManager.asyncSession()
      .executeReadAsync(tx => tx.runAsync(query)
        .thenCompose(cursor => cursor.forEachAsync(record => {
          val person = Person.fromRecord(record, "p")
          persons += person
        })
          .thenCompose(_ => {
            result.complete(persons)
            result
          })))
    logger.info("<==findAllPersons")
    result

  private def findAllPersons2(): CompletionStage[List[Person]] =
    logger.info("==>findAllPersons2")
    val result = CompletableFuture[List[Person]]
    val query = s"MATCH (p:Person) RETURN (p)"
    var persons = List[Person]()
    SessionManager.asyncSession()
      .executeReadAsync(tx => tx.runAsync(query)
        .thenCompose(cursor => cursor.forEachAsync(record => {
          val person = Person.fromRecord(record, "p")
          persons = person :: persons
        })
          .thenCompose(_ => {
            result.complete(persons)
            result
          })))
    logger.info("<==findAllPersons2")
    result

  def main(args: Array[String]): Unit =
    logger.info("==>main")

    findOnePerson("Patrick").handle((person, error) => {
      logger.info("person: {}", person)
      logger.info("error: {}", error)
    })

    findAllPersons().handle((persons,error) =>{
      logger.info("error: {}", error)
      logger.info("Persons:")
      persons.foreach(person => logger.info("{}", person))
    })

    findAllPersons2().handle((persons,error) =>{
      logger.info("error: {}", error)
      logger.info("Persons:")
      persons.foreach(person => logger.info("{}", person))
    })

    Thread.sleep(1_000)
