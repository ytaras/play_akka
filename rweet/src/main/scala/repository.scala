package rweet

import scala.concurrent.Future
import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait repository extends persistence with model {
  object c {
    def followers(u: User) = s"user:${u.id}:followers"
    def followed(u: User) = s"user:${u.id}:followed"
    def userWall(u: User) = s"user.${u.id}.wall"
    def hashWall(t: HashTag) = s"tag.${t.tag}.wall"
  }
}

trait UserFollow { self: repository =>

  def follow(by: User, of: User) =
    for {
      _ <- client.sadd(c.followers(of), by)
      _ <- client.sadd(c.followed(by), of)
    } yield ()

  def followers(of: User) =
    client.smembers[User](c.followers(of))
  def followed(by: User) =
    client.smembers[User](c.followed(by))
}

trait SendRweet { self: repository with UserFollow =>
  def sendRweet(rweet: Rweet) =
    for {
      fs <- followers(rweet.author)
      targets = fs + rweet.author ++ rweet.users
      _  <- sendToUsers(rweet, targets)
      _  <- sendToTags(rweet, rweet.tags)
    } yield ()

  private def sendToUsers(rweet: Rweet, users: Set[User]) = {
    val pushes = users.map {
      u => client.lpush(c.userWall(u), rweet)
    }
    Future.sequence(pushes).map { _ => () }
  }

  private def sendToTags(rweet: Rweet, tags: Set[HashTag]) = {
    val pushes = tags.map {
      tag => client.lpush(c.hashWall(tag), rweet)
    }
    Future.sequence(pushes).map { _ => () }
  }

}

trait FindRweets { self: repository =>
  def userWall(of: User) =
    client.lrange[Rweet](c.userWall(of), 0, 100)
  def hashTags(tag: HashTag) =
    client.lrange[Rweet](c.hashWall(tag), 0, 100)
}

trait api extends SendRweet with UserFollow with FindRweets
    with repository

object Test extends App {
  object ApiInstance extends api with model with persistence
  import ApiInstance._
  import scala.concurrent.duration._
  import scala.concurrent.Await

  val u1 = User("user1")
  val u2 = User("user2")
  val u3 = User("user3")
  val u4 = User("user4")
  val followU = for {
    _    <- client.flushdb()
    _    <- follow(u1, u2)
    _    <- follow(u3, u2)
    fs   <- followers(u2)
    fr   <- followed(u1)
    _    <- sendRweet(Rweet.parse("Hello, @user4, we're talking about #stuff",
      u2))
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
  followU onFailure {
    case e =>
      system.shutdown
      throw e
  }

}
