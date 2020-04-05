import sbt.Keys.{testFrameworks, _}

name := "pwa.pool"

val akkaVersion = "2.6.4"
val akkkHttpVersion = "10.1.11"
val quillVersion = "3.5.1"
val upickleVersion = "1.0.0"
val scalaTestVersion = "3.1.1"

val jsCompileMode = fastOptJS  // fullOptJS

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.11"
)

lazy val tripletail = project.in(file("."))
  .aggregate(shared.js, shared.jvm, js, sw, jvm)
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

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.9.0",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC5",
      "com.lihaoyi" %%% "utest" % "0.7.4" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .settings(
    jsEnv in Test := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  ) dependsOn shared.js

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.10.0"
    )
  )

lazy val jvm = (project in file("jvm"))
  .configs(IntegrationTest)
  .settings(commonSettings)
  .settings(
    Defaults.itSettings,
    mainClass in reStart := Some("tripletail.Server"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-agent" % "2.5.30",
      "de.heikoseeberger" %% "akka-http-upickle" % "1.31.0",
      "io.getquill" %% "quill-sql" % quillVersion,
      "io.getquill" %% "quill-async-postgres" % quillVersion,
      "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
      "org.jodd" % "jodd-mail" % "5.1.4",
      "com.typesafe" % "config" % "1.4.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % IntegrationTest,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % IntegrationTest,
      "org.scalatest" %% "scalatest" % scalaTestVersion % IntegrationTest
    ),
    scalacOptions += "-Ywarn-macros:after",
    javaOptions in IntegrationTest += "-Dquill.binds.log=true"
  ) dependsOn(shared.js, shared.jvm, js, sw)