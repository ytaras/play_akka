package rweet

import scala.concurrent.Future
import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait UserFollow { self: persistence with model =>

  def followUser(by: User, of: User): Future[Boolean] =
    for {
      // TODO It's sequential?
      followed <- client.sadd(s"user:${of.id}:followers", by)
      follower <- client.sadd(s"user:${by.id}:followed", of)
    } yield true


  def followers(of: User): Future[Set[User]] =
    client.smembers(s"user:${of.id}:followers")
  def followed(by: User): Future[Set[User]] =
    client.smembers(s"user:${by.id}:followed")

}

trait SendRweet { self: persistence with model with UserFollow =>
  def sendRweet(rweet: Rweet): Future[Boolean] =
    for {
      fs <- followers(rweet.author)
      _  <- sendToUsers(rweet, fs + rweet.author)
    } yield true

  def sendToUsers(rweet: Rweet, users: Set[User]): Future[Boolean] = {
    val pushes = users.map {
      user => client.lpush(s"user.${user.id}.wall", rweet)
    }
    Future.sequence(pushes).map { _ => true }
  }

}

trait FindRweets { self: persistence with model =>
  def userWall(of: User) =
    client.lrange[Rweet](s"user.${of.id}.wall", 0, 100)
  def hashTags(tag: HashTag): Future[List[Rweet]] =
    ???
}

trait api extends SendRweet with UserFollow with FindRweets {
  self: model with persistence =>
}

object Test extends App {
  object ApiInstance extends api with model with persistence
  import ApiInstance._
  import scala.concurrent.duration._
  import scala.concurrent.Await

  val u1 = User("user1")
  val u2 = User("user2")
  val u3 = User("user3")
  val follow = for {
    _    <- client.flushdb()
    _    <- followUser(u1, u2)
    _    <- followUser(u3, u2)
    fs   <- followers(u2)
    fr   <- followed(u1)
    _    <- sendRweet(Rweet("message", Nil, Nil, u2))
    wall <- userWall(u1)
    wal2 <- userWall(u2)
  } {
    println(s"Followers of ${u2}: ${fs}")
    println(s"Followed by ${u1}: ${fr}")
    println(s"Wall of ${u1}: ${wall}")
    println(s"Wall of ${u2}: ${wal2}")
    system.shutdown
  }


}
