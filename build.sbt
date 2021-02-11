val reactVersion = "16.14.0"
val scalaJSReactVersion = "1.7.7"
val scalaCssVersion = "0.7.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "spoter.me",
      scalaVersion := "2.13.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "spot.POD",
    scalacOptions += "-feature",
    scalacOptions += "-language:higherKinds",
    scalacOptions += "-Xfatal-warnings",
    scalacOptions += "-deprecation",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion,
      "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,
      "com.payalabs" %%% "scalajs-react-bridge" % "0.8.5",
      "org.scala-js" %%% "scalajs-java-time" % "1.0.0",
      //"com.beachape" %%% "enumeratum" % "1.5.13",
      "org.typelevel" %%% "cats-core" % "2.1.1",
      //"org.typelevel" %%% "cats-macros" % "1.6.0",
      //"org.typelevel" %%% "cats-kernel" % "1.6.0",
      "com.softwaremill.sttp.client" %%% "core" % "2.2.9",
      "org.scalatest" %%% "scalatest" % "3.2.2" % Test
    ),
    npmDependencies in Compile ++= Seq(
      "react" -> reactVersion,
      "react-dom" -> reactVersion,
      "@solid/react" -> "1.6.0",
      "@inrupt/solid-react-components" -> "0.4.0",
      "rdflib" -> "0.19.1",
      "react-bootstrap" -> "1.0.0-beta.8"),
  ).enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
