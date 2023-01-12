/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import com.stulsoft.neo4js.exercises.GettingInfoEx1.logger
import com.stulsoft.neo4js.session.SessionManager
import com.typesafe.scalalogging.StrictLogging
import org.neo4j.driver.internal.value.NodeValue

import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Using}

object GettingInfoEx1 extends StrictLogging:

  private def showNode(): Unit =
    logger.info("==>showNode")
    Using(SessionManager.session()) {
      session => {
        session.executeRead(tx => {
          val query = "MATCH (p:Person) RETURN (p)"
          val result = tx.run(query)
          val record = result.peek()
          logger.info("record: {}", record.toString)
          val node = record.values().get(0).asNode()
          logger.info("node: {}", node)
          logger.info("node.elementId(): {}", node.elementId())

          logger.info("labels:")
          node.labels().forEach(l => logger.info("{}", l))

          logger.info("keys:")
          node.keys().forEach(k => logger.info("{}", k))

          logger.info("values:")
          node.values().forEach(v => logger.info("{}", v))

          val map =node.asMap().asScala
          logger.info("map:")
//          map.foreach((item) => logger.info("{} -> {}", item._1, item._2))
          map.foreach((name, value) => logger.info("{} -> {}", name, value))
        })
      }
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  private def fetchNonexistentParameter():Unit =
    logger.info("==>fetchNonexistentParameter")

    Using(SessionManager.session()) {
      session => {
        session.executeRead(tx => {
          val query = "MATCH (p:Person) RETURN (p)"
          val result = tx.run(query)
          val record = result.peek()
          val value = record.get("p")
//          val nonexistent = value.get("nonexistent").asString()
          val nonexistent = value.get("nonexistent", "")
          logger.info("nonexistent: {}", nonexistent)
        })
      }
    } match
      case Success(_) =>
      case Failure(exception) => logger.error(exception.getMessage, exception)

  def main(args: Array[String]): Unit =
      logger.info("==>main")
      showNode()
      fetchNonexistentParameter()

      SessionManager.closeDriver()