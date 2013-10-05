import sbt._
import Keys._

object RweeterBuild extends Build {
  val sprayVersion = "1.2-20130928"
  lazy val root = Project(id = "rweeter", base = file(".")).
    aggregate(common, akka)

  lazy val common = Project(id = "rweeter-common", base = file("common"))

  lazy val akka = Project(id = "rweeter-akka",
    base = file("rweet")) dependsOn (common % "test->test;compile->compile")

}
