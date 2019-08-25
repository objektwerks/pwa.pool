import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

name := "tripletail"

val akkaVersion = "2.5.24"
val akkkHttpVersion = "10.1.9"
val quillVersion = "3.4.3"
val circeVersion =  "0.11.1"
val scalaTestVersion = "3.0.8"

val jsCompileMode = fastOptJS  // fullOptJS

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.9"
)

lazy val tripletail = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, js, sw, jvm)
  .settings(commonSettings)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )
lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.7.1",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  ) dependsOn sharedJS

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.9.4"
    )
  )

lazy val jvm = (project in file("jvm"))
  .configs(IntegrationTest)
  .settings(commonSettings)
  .settings(
    Defaults.itSettings,
    mainClass in reStart := Some("tripletail.PoolApp"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.27.0",
      "io.getquill" %% "quill-sql" % quillVersion,
      "io.getquill" %% "quill-async-postgres" % quillVersion,
      "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
      "com.typesafe" % "config" % "1.3.4",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % IntegrationTest,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % IntegrationTest,
      "org.scalatest" %% "scalatest" % scalaTestVersion % IntegrationTest
    ),
    scalacOptions ++= Seq(
      "-Ywarn-macros:after"
    ),
    javaOptions in IntegrationTest += "-Dquill.binds.log=true",
    (resources in Compile) += (jsCompileMode in (sharedJS, Compile)).value.data,
    (resources in Compile) += (jsCompileMode in (js, Compile)).value.data,
    (resources in Compile) += (jsCompileMode in (sw, Compile)).value.data
  ) dependsOn(sharedJS, sharedJVM, js, sw)