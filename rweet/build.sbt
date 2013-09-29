name := "rweet"

version := "1.0"

scalaVersion := "2.10.2"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisreact" % "0.2",
  "io.spray"    %%  "spray-json"     % "1.2.5",
  "org.specs2" %% "specs2" % "2.2.2" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.0.M8" % "test"
)
