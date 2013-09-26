package rweet

import scala.concurrent.Future
import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait UserFollow { self: persistence with model =>

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

trait SendRweet { self: persistence with model =>
  def sendRweet(rweet: Rweet): Future[Boolean] =
    client.lpush(s"user.")

}

trait FindRweets { self: persistence with model =>
  def userWall(of: User): Future[List[Rweet]]
  def hashTags(tag: HashTag): Future[List[Rweet]]
}
