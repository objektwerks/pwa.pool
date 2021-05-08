name := "scalajs.pool"

lazy val upickleVersion = "1.3.11"
lazy val scalaTestVersion = "3.2.8"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.5"
)

lazy val pool = project.in(file("."))
  .aggregate(sharedJs, sharedJvm, js, jvm)
  .settings(common)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % upickleVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm

lazy val public = "scala-2.13/classes/public"

import NativePackagerHelper._

lazy val js = (project in file("js"))
  .dependsOn(sharedJs)
  .enablePlugins(ScalaJSPlugin, UniversalPlugin)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.13.0",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.2.1"
    ),
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := target.value / public / "js",
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory := target.value / public / "js",
    fastLinkJS / crossTarget := target.value / public,
    fullLinkJS / crossTarget := target.value / public,
    Universal / mappings := (Universal / mappings).value ++ contentOf(target.value / public)
  )

lazy val jvm = (project in file("jvm"))
  .dependsOn(sharedJvm)
  .enablePlugins(JavaServerAppPackaging)
  .configs(IntegrationTest)
  .settings(common)
  .settings(
    Defaults.itSettings,
    reStart / mainClass := Some("pool.Server"),
    libraryDependencies ++= {
      val akkaVersion = "2.6.14"
      val akkkHttpVersion = "10.2.4"
      val quillVersion = "3.7.0"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "de.heikoseeberger" %% "akka-http-upickle" % "1.36.0",
        "ch.megard" %% "akka-http-cors" % "1.1.1",
        "io.getquill" %% "quill-sql" % quillVersion,
        "io.getquill" %% "quill-async-postgres" % quillVersion,
        "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
        "org.jodd" % "jodd-mail" % "6.0.5",
        "com.typesafe" % "config" % "1.4.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % IntegrationTest,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % IntegrationTest,
        "org.scalatest" %% "scalatest" % scalaTestVersion % IntegrationTest
      )
    },
    scalacOptions ++= Seq("-Ywarn-macros:after"),
    IntegrationTest / javaOptions += "-Dquill.binds.log=true"
  )