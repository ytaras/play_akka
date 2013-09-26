package rweet

import scala.concurrent.Future

trait UserFollow { self: persistence with model =>
  import spray.json.DefaultJsonProtocol._
  import com.redis.serialization.SprayJsonSupport._
  import com.redis.serialization._

  def followUser(from: User, to: User): Future[Boolean]
  def followers(of: User): Future[List[User]]
  def followed(by: User): Future[Set[User]] =
    client.smembers(s"user:${by}:followed")

  implicit val userFormat = jsonFormat1(User)
}
