import sbt._
import Keys._

object RweeterBuild extends Build {
  val sprayVersion = "1.2-20130928"
  lazy val root = Project(id = "rweeter", base = file(".")).
    aggregate(rweeterAkka)

  lazy val rweeterAkka = Project(id = "rweeter-akka",
    base = file("rweet"))

}
