name := "rweet"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisreact"     % "0.2",
  "io.spray"      %% "spray-json"     % "1.2.5",
  "io.spray"      %  "spray-routing"  % sprayVersion,
  "io.spray"      %  "spray-can"      % sprayVersion,
  "io.spray"      %  "spray-testkit"  % sprayVersion % "test",
  "org.scalatest" % "scalatest_2.10"  % "2.0.M8" % "test"
)
