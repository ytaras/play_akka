name := "rweet"

version := "1.0"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "spray" at "http://repo.spray.io/",
  "nightly-spray" at "http://nightlies.spray.io/"
)

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisreact" % "0.2",
  "io.spray"    %%  "spray-json"     % sprayVersion,
  "io.spray"    %   "spray-routing"  % "1.2-20130928",
  "io.spray"    %   "spray-can"      % "1.2-20130928",
  "org.scalatest" % "scalatest_2.10" % "2.0.M8" % "test"
)
