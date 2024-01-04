ThisBuild / scalaVersion := "3.3.1"
ThisBuild / version := "0.2.0"
ThisBuild / organization := "com.stulsoft"
ThisBuild / organizationName := "stulsoft"

lazy val root = (project in file("."))
  .settings(
    name := "ys-neo4j-scala",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.7",
    libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "5.6.0",

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
  )