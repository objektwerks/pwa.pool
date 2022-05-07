addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.9.0") // upgrading to 1.10.0 breaks js IR build!
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.3.1")

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.9")
