name := "pwa.pool"

val akkaVersion = "2.6.4"
val akkkHttpVersion = "10.1.11"
val quillVersion = "3.5.1"
val upickleVersion = "1.0.0"
val scalaTestVersion = "3.1.1"

val jsCompileMode = fastOptJS  // fullOptJS

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.11"
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
      "com.raquo" %%% "domtypes" % "0.10.0"
    )
  )

lazy val js = (project in file("js"))
  .aggregate(sharedJs, sw)
  .enablePlugins(ScalaJSPlugin)
  .settings(common)
  .settings(
    maintainer := "pool@gmail.com",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.9.0",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC5",
      "com.lihaoyi" %%% "utest" % "0.7.4" % Test
    ),
    (resources in Compile) += (jsCompileMode in (sharedJs, Compile)).value.data,
    (resources in Compile) += (jsCompileMode in (sw, Compile)).value.data,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsEnv in Test := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  )
  .dependsOn(sharedJs, sw)

lazy val jvm = (project in file("jvm"))
  .aggregate(sharedJvm)
  .configs(IntegrationTest)
  .settings(common)
  .settings(
    Defaults.itSettings,
    mainClass in reStart := Some("pool.Server"),
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-agent" % "2.5.31",
      "de.heikoseeberger" %% "akka-http-upickle" % "1.32.0",
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
    scalacOptions ++= Seq("-Ywarn-macros:after"),
    javaOptions in IntegrationTest += "-Dquill.binds.log=true",
  )
  .dependsOn(sharedJvm)