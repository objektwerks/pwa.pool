import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

name := "tripletail"

val akkaVersion = "2.5.25"
val akkkHttpVersion = "10.1.10"
val quillVersion = "3.4.10"
val upickleVersion = "0.8.0"
val scalaTestVersion = "3.0.8"

val jsCompileMode = fastOptJS  // fullOptJS

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.10"
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
      "com.lihaoyi" %% "upickle" % upickleVersion,
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
      "com.raquo" %%% "domtypes" % "0.9.5",
      "com.lihaoyi" %%% "upickle" % upickleVersion
    )
  ) dependsOn sharedJS

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.9.5"
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
      "com.typesafe.akka" %% "akka-agent" % akkaVersion,
      "de.heikoseeberger" %% "akka-http-upickle" % "1.29.1",
      "io.getquill" %% "quill-sql" % quillVersion,
      "io.getquill" %% "quill-async-postgres" % quillVersion,
      "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
      "org.jodd" % "jodd-mail" % "5.1.0-20190624",
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