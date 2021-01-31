name := "pwa.pool"

lazy val upickleVersion = "1.2.3"
lazy val scalaTestVersion = "3.2.3"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.4"
)

lazy val pool = project.in(file("."))
  .aggregate(sharedJs, sharedJvm, sw, js, jvm)
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

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(common)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.12.0"
    )
  )

lazy val jsOptCompileMode = fastOptJS  // fullOptJS
lazy val jsOptDir = "web/classes/main/META-INF/resources/webjars/js/0.1-SNAPSHOT"
lazy val jsOptFile = "js-fastopt.js"

import NativePackagerHelper._

lazy val js = (project in file("js"))
  .dependsOn(sharedJs, sw)
  .enablePlugins(ScalaJSPlugin, SbtWeb, UniversalPlugin)
  .settings(common)
  .settings(
    maintainer := "pool@gmail.com",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.11.0",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.1.0",
      "com.lihaoyi" %%% "utest" % "0.7.7" % Test
    ),
    Assets / resources += (jsOptCompileMode in (sharedJs, Compile)).value.data,
    Assets / resources += (jsOptCompileMode in (sw, Compile)).value.data,
    artifactPath in(Compile, jsOptCompileMode) := target.value / jsOptDir / jsOptFile,
    mappings in Universal := (mappings in Universal).value ++ directory(target.value / jsOptDir),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsEnv in Test := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  )

lazy val jvm = (project in file("jvm"))
  .dependsOn(sharedJvm)
  .enablePlugins(JavaServerAppPackaging)
  .configs(IntegrationTest)
  .settings(common)
  .settings(
    Defaults.itSettings,
    mainClass in reStart := Some("pool.Server"),
    libraryDependencies ++= {
      val akkaVersion = "2.6.12"
      val akkkHttpVersion = "10.2.3"
      val quillVersion = "3.6.0"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "de.heikoseeberger" %% "akka-http-upickle" % "1.35.3",
        "io.getquill" %% "quill-sql" % quillVersion,
        "io.getquill" %% "quill-async-postgres" % quillVersion,
        "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
        "org.jodd" % "jodd-mail" % "6.0.4",
        "com.typesafe" % "config" % "1.4.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % IntegrationTest,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % IntegrationTest,
        "org.scalatest" %% "scalatest" % scalaTestVersion % IntegrationTest
      )
    },
    scalacOptions ++= Seq("-Ywarn-macros:after"),
    javaOptions in IntegrationTest += "-Dquill.binds.log=true"
  )
