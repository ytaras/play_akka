scalaVersion := "2.10.2"

resolvers in ThisBuild ++= Seq(
  "spray" at "http://repo.spray.io/",
  "nightly-spray" at "http://nightlies.spray.io/"
)

parallelExecution in ThisBuild := false
