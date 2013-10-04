package rweet

import spray.routing.HttpService
import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App with Redis {

  val service = system.actorOf(Props[DemoServiceActor])

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)
}

class DemoServiceActor() extends Actor with DemoService {
  def actorRefFactory = context

  def receive = runRoute(route)
}

trait DemoService extends HttpService {
  import spray.json.DefaultJsonProtocol._
  import spray.httpx.marshalling._
  import spray.httpx.SprayJsonSupport._

  // TODO Find correct layout of a cake pattern
  val aaa = new RedisRepository {}
  case class UserGodObject(id: String)
  implicit val userGOMarshaller = jsonFormat1(UserGodObject)
  val route = {
    get {
      path("hello") {
        complete("World")
      } ~
      path("users" / Segment) { s =>
        complete(UserGodObject(s))
      } ~
      path("users" / Segment / "followers") { s =>
        complete {
          import aaa._
          followers(User(s))
        }
      }
    }
  }
}
