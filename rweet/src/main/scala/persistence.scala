package rweet

trait persistence {
  import com.redis._
  import akka.actor.ActorSystem
  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val system = ActorSystem("redis-client")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val client = RedisClient("localhost", 6379)

}
