name := """euromillions-generator"""

version := "1.0"

scalaVersion := "2.11.7"

mainClass in (Compile, run) := Some("com.pt.pedrorijo91.euromillions.Application")

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.clapper" %% "argot" % "1.0.3"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.0"

libraryDependencies += "com.google.code.gson" % "gson" % "1.7.1"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"
