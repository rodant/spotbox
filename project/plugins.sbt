// Comment to get more information during initialization
logLevel := Level.Warn

// Resolvers
//resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Sbt plugins

// Use Scala.js 0.6.x
addSbtPlugin("org.scala-js"                 % "sbt-scalajs"               % "0.6.28")
addSbtPlugin("ch.epfl.scala"                % "sbt-scalajs-bundler"       % "0.14.0")
// If you prefer using Scala.js 1.x, uncomment the following plugins instead:
// addSbtPlugin("com.vmunier"               % "sbt-web-scalajs"           % "1.0.8")
// addSbtPlugin("org.scala-js"              % "sbt-scalajs"               % "1.0.0-M3")

//addSbtPlugin("org.portable-scala"        % "sbt-scalajs-crossproject"  % "0.5.0")
