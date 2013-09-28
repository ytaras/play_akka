package rweet

import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._
import spray.json.DefaultJsonProtocol._

trait persistence { self: model =>
  import com.redis._
  import akka.actor.ActorSystem
  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val system = ActorSystem("redis-client")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val client = RedisClient("localhost", 6379)

  implicit val userFormat = jsonFormat1(User)
  implicit val hashTagFormat = jsonFormat1(HashTag)
  implicit val rweetFormat = jsonFormat4(Rweet.apply)
}
