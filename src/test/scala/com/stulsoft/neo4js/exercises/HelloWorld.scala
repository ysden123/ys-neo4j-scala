/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.exercises

import com.stulsoft.neo4js.config.Configuration
import com.stulsoft.neo4js.session.SessionManager
import com.stulsoft.neo4js.session.SessionManager.session
import com.typesafe.scalalogging.StrictLogging
import org.neo4j.driver.Values.parameters
import org.neo4j.driver.{AuthTokens, GraphDatabase, Query}

import scala.util.{Success, Using}

object HelloWorld extends StrictLogging:
  private def clear(): Unit =
    logger.info("==>clear")
    val session = SessionManager.session()
    try
      val query = new Query("MATCH(n:Greeting) DELETE(n)")
      val result = session.run(query).consume()
      logger.info("Deleted {} nodes", result.counters().nodesDeleted())
    catch
      case exception: Exception =>
        logger.error(exception.getMessage, exception)
    session.close()

  private def test1(): Unit =
    logger.info("==>test1")

    val session = SessionManager.session()
    val message = "hello, world"
    val greeting = session.executeWrite(tx => {
      logger.info("Inside session.executeWrite")
      val query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)",
        parameters("message", message))
      val result = tx.run(query)
      result.single().get(0).asString()
    })
    logger.info("result: {}", greeting)
    session.close()

  def main(args: Array[String]): Unit =
    logger.info("==>main")
    try
      clear()
      test1()
      clear()
    catch
      case exception: Exception =>
        logger.error(exception.getMessage, exception)
    finally
      logger.info("In finally")
      SessionManager.closeDriver()
