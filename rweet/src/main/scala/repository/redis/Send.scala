package rweet.repository.redis

import rweet.{Redis, Repository}
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._
import scala.concurrent.Future

trait Send extends Redis with Repository with Load {
  def c: WallCollections

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
