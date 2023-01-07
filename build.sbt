ThisBuild / scalaVersion := "3.2.1"
ThisBuild / version := "0.0.1"
ThisBuild / organization := "com.stulsoft"
ThisBuild / organizationName := "stulsoft"

lazy val root = (project in file("."))
  .settings(
    name := "ys-neo4j-scala",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.5",
	libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "5.3.1",

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test
  )