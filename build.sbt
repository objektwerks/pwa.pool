name := "pwa.pool"

lazy val akkaVersion = "2.6.15"
lazy val akkkHttpVersion = "10.2.4"
lazy val quillVersion = "3.8.0"
lazy val upickleVersion = "1.4.0"
lazy val scalaTestVersion = "3.2.9"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.6"
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
      "io.github.cquiroz" %% "scala-java-time" % "2.3.0",
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
      "com.raquo" %%% "laminar" % "0.13.1",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.3.0"
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
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "de.heikoseeberger" %% "akka-http-upickle" % "1.37.0",
        "io.getquill" %% "quill-sql" % quillVersion,
        "io.getquill" %% "quill-async-postgres" % quillVersion,
        "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
        "org.jodd" % "jodd-mail" % "6.0.5",
        "com.typesafe" % "config" % "1.4.1",
        "ch.qos.logback" % "logback-classic" % "1.2.5",
        "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % IntegrationTest,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % IntegrationTest,
        "org.scalatest" %% "scalatest" % scalaTestVersion % IntegrationTest
      )
    },
    scalacOptions ++= Seq("-Ywarn-macros:after")
  )