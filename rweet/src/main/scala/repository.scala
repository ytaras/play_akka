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
