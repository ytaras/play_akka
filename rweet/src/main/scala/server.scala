package rweet

import spray.routing.HttpService
import akka.actor.{Actor, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App with api {

  val service = system.actorOf(Props[DemoServiceActor])

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)
}

class DemoServiceActor extends Actor with DemoService {
  def actorRefFactory = context

  def receive = runRoute(demoRoute)
}

trait DemoService extends HttpService {
  val demoRoute = {
    get {
      path("hello") {
        complete("World")
      } ~
      path("users" / Segment) { s =>
        complete(s)
      }
    }
  }
}
