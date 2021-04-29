name := "scalajs.pool"

lazy val upickleVersion = "1.3.11"
lazy val scalaTestVersion = "3.2.8"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.5"
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
  .enablePlugins(SbtWeb)
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
  .enablePlugins(ScalaJSPlugin, SbtWeb)
  .settings(common)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.14.2"
    )
  )

lazy val js = (project in file("js"))
  .dependsOn(sharedJs, sw)
  .enablePlugins(ScalaJSPlugin, SbtWeb)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.12.2",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.2.1"
    )
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