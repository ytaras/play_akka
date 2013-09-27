package rweet

import scala.concurrent.Future
import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait UserFollow { self: persistence with model =>

  def followUser(by: User, of: User) =
    for {
      followed <- client.sadd(s"user:${of.id}:followers", by)
      follower <- client.sadd(s"user:${by.id}:followed", of)
    } yield ()


  def followers(of: User) =
    client.smembers[User](s"user:${of.id}:followers")
  def followed(by: User) =
    client.smembers[User](s"user:${by.id}:followed")

}

trait SendRweet { self: persistence with model with UserFollow =>
  def sendRweet(rweet: Rweet) =
    for {
      fs <- followers(rweet.author)
      targets = fs + rweet.author ++ rweet.users
      _  <- sendToUsers(rweet, targets)
      _  <- sendToTags(rweet, rweet.tags)
    } yield println(targets)

  def sendToUsers(rweet: Rweet, users: Set[User]) = {
    val pushes = users.map {
      user => client.lpush(s"user.${user.id}.wall", rweet)
    }
    Future.sequence(pushes).map { _ => () }
  }

  def sendToTags(rweet: Rweet, tags: Set[HashTag]) = {
    val pushes = tags.map {
      tag => client.lpush(s"tag.${tag.tag}.wall", rweet)
    }
    Future.sequence(pushes).map { _ => () }
  }

}

trait FindRweets { self: persistence with model =>
  def userWall(of: User) =
    client.lrange[Rweet](s"user.${of.id}.wall", 0, 100)
  def hashTags(tag: HashTag) =
    client.lrange[Rweet](s"tag.${tag.tag}.wall", 0, 100)
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
  val u4 = User("user4")
  val follow = for {
    _    <- client.flushdb()
    _    <- followUser(u1, u2)
    _    <- followUser(u3, u2)
    fs   <- followers(u2)
    fr   <- followed(u1)
    _    <- sendRweet(rweetOf(u2, "Hello, @user4, we're talking about #stuff"))
    wall <- userWall(u1)
    wal2 <- userWall(u2)
    wal4 <- userWall(u4)
    hs   <- hashTags(HashTag("stuff"))
  } yield {
    println(s"Followers of ${u2}: ${fs}")
    println(s"Followed by ${u1}: ${fr}")
    println(s"Wall of ${u1}: ${wall}")
    println(s"Wall of ${u2}: ${wal2}")
    println(s"Wall of ${u4}: ${wal4}")
    println(s"Hash tags for #stuff: ${hs}")
    system.shutdown
  }
  follow onFailure {
    case e =>
      system.shutdown
      throw e
  }

}
