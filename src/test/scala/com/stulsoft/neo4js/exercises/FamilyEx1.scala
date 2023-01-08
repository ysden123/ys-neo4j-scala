/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import scala.jdk.CollectionConverters.*
import com.stulsoft.neo4js.session.SessionManager
import com.typesafe.scalalogging.StrictLogging

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object FamilyEx1 extends StrictLogging:
  private def createFamily(): Unit =
    logger.info("==>createFamily")
    val session = SessionManager.session()

    try
      session.executeWrite(tc => {
        val clearQuery = "MATCH(n:Person) DETACH DELETE(n)"
        logger.info("Clearing DB (delete all Person nodes")
        tc.run(clearQuery)
      })

      session.executeWrite(tc => {
        logger.info("Creating constraint")
        val createConstrainQuery = "CREATE CONSTRAINT person_name IF NOT EXISTS FOR (p:Person) REQUIRE p.name IS UNIQUE"
        tc.run(createConstrainQuery)
      })

      session.executeWrite(tc => {
        logger.info("Adding couples")
        var addCoupleQuery = "CREATE (p1:Person {name: 'Adam', sex: 'male'}) -[r1:SPOUSE]-> (p2:Person {name: 'Sara', sex: 'female'}) -[r2:SPOUSE]-> (p1)"
        tc.run(addCoupleQuery)

        addCoupleQuery = "CREATE (p1:Person {name: 'Smith', sex: 'male'}) -[r1:SPOUSE]-> (p2:Person {name: 'Donna', sex: 'female'}) -[r2:SPOUSE]-> (p1)"
        tc.run(addCoupleQuery)

        // Error: duplicated 'Smith' Person
        /*
                addCoupleQuery = "CREATE (p1:Person {name: 'Smith', sex: 'male'}) -[r1:SPOUSE]-> (p2:Person {name: 'Lisa', sex: 'female'}) -[r2:SPOUSE]-> (p1)"
                tx.run(addCoupleQuery)
        */
      })
    catch
      case exception: Exception => logger.info(exception.getMessage, exception)
    finally
      session.close()

  private def addManyCouples(): Unit =
    logger.info("==>addManyCouples")
    val session = SessionManager.session()
    try
      session.executeWrite(tc => {
        List(
          (Person("Patrick", "male"), Person("Hellen", "female")),
          (Person("Anderson", "male"), Person("Stella", "female")),
        ).foreach(couple => {
          val addCoupleQuery = s"CREATE (p1: ${couple._1}) -[r1:SPOUSE]-> (p2: ${couple._2}) -[r2:SPOUSE]-> (p1)"
          tc.run(addCoupleQuery)
        })
      })
    catch
      case exception: Exception => logger.info(exception.getMessage, exception)
    finally
      session.close()

  private def addManySingles(): Unit =
    logger.info("==>addManySingles")
    val session = SessionManager.session()
    try
      session.executeWrite(tc => {
        List(Person("PatrickS", "male"),
          Person("HellenS", "female"),
          Person("AndersonS", "male"),
          Person("StellaS", "female"),
        ).foreach(person => {
          val addSingleQuery = s"CREATE (p1: $person)"
          tc.run(addSingleQuery)
        })
      })
    catch
      case exception: Exception => logger.info(exception.getMessage, exception)
    finally
      session.close()

  private def findAllSingles(): Unit =
    logger.info("==>findAllSingles")
    val session = SessionManager.session()
    try
      session.executeRead(tc => {
        val findAllSinglesQuery = "MATCH (p:Person) WHERE not exists((p)-[:SPOUSE]->()) RETURN p"
        tc.run(findAllSinglesQuery)
          .list()
          .asScala
          .map(record => Person.fromRecord(record, "p"))
          .foreach(person => logger.info("{}", person))
      })
    catch
      case exception: Exception => logger.info(exception.getMessage, exception)
    finally
      session.close()

  private def findAllSingles2(): Unit =
    logger.info("==>findAllSingles2")
    val session = SessionManager.session()
    try
      session.executeRead(tc => {
        val findAllSinglesQuery = "MATCH (p:Person) WHERE not exists((p)<-[:SPOUSE]-()) RETURN p"
        tc.run(findAllSinglesQuery)
          .list()
          .asScala
          .map(record => Person.fromRecord(record, "p"))
          .foreach(person => logger.info("{}", person))
      })
    catch
      case exception: Exception => logger.info(exception.getMessage, exception)
    finally
      session.close()

  def main(args: Array[String]): Unit =
    logger.info("==>main")
    val start = System.currentTimeMillis()
    createFamily()
    addManyCouples()
    addManySingles()
    findAllSingles()
    findAllSingles2()
    logger.info("Completed in {}", Duration(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS))
    SessionManager.closeDriver()

