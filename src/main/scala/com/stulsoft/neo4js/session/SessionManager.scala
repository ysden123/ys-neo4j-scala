/*
 * Copyright (c) 2023. StulSoft
 */

package com.stulsoft.neo4js.session

import com.stulsoft.neo4js.config.Configuration
import com.typesafe.scalalogging.StrictLogging
import org.neo4j.driver.{AuthTokens, GraphDatabase, Session}

import scala.util.{Failure, Success, Try}

object SessionManager extends StrictLogging:
  private lazy val driver = GraphDatabase.driver(Configuration.uri, AuthTokens.basic(Configuration.username, Configuration.password))

  def session(): Session =
    driver.session()

  def closeDriver(): Unit =
    driver.close()