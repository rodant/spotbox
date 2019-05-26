val reactVersion = "16.7.0"
val scalaJSReactVersion = "1.4.2"
val scalaCssVersion = "0.5.6"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "spoter.me",
      scalaVersion := "2.12.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "SPOTBox",
    scalacOptions += "-feature",
    scalacOptions += "-language:higherKinds",
    scalacOptions += "-Ypartial-unification",
    scalaJSUseMainModuleInitializer := true,
    // creates single js resource file for easy integration in html page
    //skip in packageJSDependencies := false,
    version in webpack := "4.28.1",
    version in startWebpackDevServer := "3.1.14",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.7",
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion,
      "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,
      "com.payalabs" %%% "scalajs-react-bridge" % "0.8.0",
      "org.scala-js" %%% "scalajs-java-time" % "0.2.5",
      "com.beachape" %%% "enumeratum" % "1.5.13",
      //"org.typelevel" %%% "cats-core" % "1.6.0",
      //"org.typelevel" %%% "cats-macros" % "1.6.0",
      //"org.typelevel" %%% "cats-kernel" % "1.6.0",
      "org.scalatest" %%% "scalatest" % "3.0.7" % Test
    ),
    npmDependencies in Compile ++= Seq(
      "react" -> reactVersion,
      "react-dom" -> reactVersion,
      "@solid/react" -> "1.6.0",
      "@inrupt/solid-react-components" -> "0.4.0",
      "rdflib" -> "0.20.1",
      "react-bootstrap" -> "1.0.0-beta.8"),
  ).enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
