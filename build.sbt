name := "pwa.pool"

val akkaVersion = "2.6.4"
val akkkHttpVersion = "10.1.11"
val quillVersion = "3.5.1"
val upickleVersion = "1.0.0"
val scalaTestVersion = "3.1.1"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.11"
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

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(common)
  .settings(
    maintainer := "pool@gmail.com",
    scalaJSProjects := Seq(js, sw),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    isDevMode in scalaJSPipeline := false, // default to fullOptJs
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    WebKeys.packagePrefix in Assets := "/",
    managedClasspath in Runtime += (packageBin in Assets).value,
      libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.9.0",
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC5",
      "com.lihaoyi" %%% "utest" % "0.7.4" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsEnv in Test := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  )
  .dependsOn(sharedJs, sw)

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(common)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.10.0"
    )
  )

lazy val jvm = (project in file("jvm"))
  .enablePlugins(JavaAppPackaging)
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

onLoad in Global := (onLoad in Global).value.andThen(state => "project js" :: state)