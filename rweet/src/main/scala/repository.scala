package rweet

import scala.concurrent.Future

trait UserFollow { self: persistence with model =>
  import spray.json.DefaultJsonProtocol._
  import com.redis.serialization.SprayJsonSupport._
  import com.redis.serialization._

  def followUser(by: User, of: User): Future[Boolean] =
    for {
      // TODO It's sequential?
      followed <- client.sadd(s"user:${of}:followers", by)
      follower <- client.sadd(s"user:${by}:followed", of)
    } yield true


  def followers(of: User): Future[Set[User]] =
    client.smembers(s"user:${of}:followers")
  def followed(by: User): Future[Set[User]] =
    client.smembers(s"user:${by}:followed")

  implicit val userFormat = jsonFormat1(User)
}
