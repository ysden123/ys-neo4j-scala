/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import com.stulsoft.neo4js.session.SessionManager
import com.typesafe.scalalogging.StrictLogging

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Using}

object FamilyEx2 extends StrictLogging:
  private lazy val couples = List(
    (Person("Patrick", "male"), Person("Hellen", "female")),
    (Person("Anderson", "male"), Person("Stella", "female")),
  )

  private lazy val singles = List(Person("PatrickS", "male"),
    Person("HellenS", "female"),
    Person("AndersonS", "male"),
    Person("StellaS", "female"),
  )

  private def createFamily(): Unit =
    logger.info("==>createFamily")
    Using(SessionManager.session()) {
      session => {
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
      }
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  private def addAllPersons(): Unit =
    logger.info("==>addAllPersons")
    Using(SessionManager.session()) {
      session =>
        session.executeWrite(tc =>
          couples.foreach(couple =>
            val addCoupleQuery = s"CREATE (:${couple._1}) CREATE (:${couple._2})"
            tc.run(addCoupleQuery)
          )

          singles.foreach(person =>
            val addPersonQuery = s"CREATE (p: $person)"
            tc.run(addPersonQuery)
          )
        )
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  private def wedding(): Unit =
    logger.info("==>wedding")
    Using(SessionManager.session()) {
      session =>
        session.executeWrite(tc =>
          couples.foreach(couple =>
            val weddingQuery =
              s"""
                 |MATCH
                 |   (a:Person),
                 |   (b:Person)
                 |WHERE a.name = '${couple._1.name}' AND b.name = '${couple._2.name}'
                 |CREATE (a) -[:SPOUSE]->(b)
                 |CREATE (b) -[:SPOUSE]->(a)
                 |""".stripMargin
            tc.run(weddingQuery)
          )
        )
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  private def findAllSingles(): Unit =
    logger.info("==>findAllSingles")
    Using(SessionManager.session()) {
      session =>
        session.executeRead(tc => {
          val findAllSinglesQuery = "MATCH (p:Person) WHERE not exists((p)-[:SPOUSE]->()) RETURN p"
          tc.run(findAllSinglesQuery)
            .list()
            .asScala
            .map(record => Person.fromValue(record.get("p")))
            .foreach(person => logger.info("{}", person))
        })
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  private def findAllSingles2(): Unit =
    logger.info("==>findAllSingles2")
    Using(SessionManager.session()) {
      session =>
        session.executeRead(tc => {
          val findAllSinglesQuery = "MATCH (p:Person) WHERE not exists((p)<-[:SPOUSE]-()) RETURN p"
          tc.run(findAllSinglesQuery)
            .list()
            .asScala
            .map(record => Person.fromRecord(record, "p"))
            .foreach(person => logger.info("{}", person))
        })
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  private def findAllCouples(): Unit =
    logger.info("==>findAllCouples")
    Using(SessionManager.session()) {
      session =>
        session.executeRead(tc => {
          val findAllSinglesQuery = "MATCH (p1:Person {sex: 'male'})-[:SPOUSE]->(p2:Person) RETURN p1,p2"
          tc.run(findAllSinglesQuery)
            .list()
            .asScala
            .map(record => (Person.fromRecord(record, "p1"), Person.fromRecord(record, "p2")))
            .foreach(persons => logger.info("{} --> {}", persons._1, persons._2))
        })
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  def main(args: Array[String]): Unit =
    logger.info("==>main")
    val start = System.currentTimeMillis()
    createFamily()
    addAllPersons()
    wedding()
    findAllSingles()
    findAllSingles2()
    findAllCouples()
    logger.info("Completed in {}", Duration(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS))
    SessionManager.closeDriver()
