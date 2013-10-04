package rweet

import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait Redis extends JsonModel {
  import com.redis._
  import akka.actor.ActorSystem
  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val system = ActorSystem("redis-client")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val client = RedisClient("localhost", 6379)
}
