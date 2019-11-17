name := "scala-chaincode"

version := "0.1"

scalaVersion := "2.12.10"

Global / cancelable := false

val akkaVer = "2.6.0"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0",
  "org.slf4j" % "slf4j-api" % "1.7.29",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.29",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.29",
  "ch.qos.logback" % "logback-core" % "1.2.3",
  "ch.qos.logback" % "logback-access" % "1.2.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0",
  "org.hyperledger.fabric-sdk-java" % "fabric-sdk-java" % "1.4.7",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % akkaVer,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVer,
  "com.typesafe.akka" %% "akka-actor" % akkaVer,
  "de.heikoseeberger" %% "akka-http-json4s" % "1.29.1",
  "org.json4s" %% "json4s-native" % "3.6.7",
)
