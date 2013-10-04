package rweet.repository.redis

import rweet.{Redis, Repository}
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._
import scala.concurrent.Future

trait Send extends Redis with Repository with Load {
  def c: WallCollections

  def sendRweet(rweet: Rweet) =
      followers(rweet.author) map { fs =>
        fs + rweet.author ++ rweet.users
      } flatMap { users =>
        sendToUsers(rweet, users) zip sendToTags(rweet, rweet.tags)
      } map { _ => () }

  private def sendToUsers(rweet: Rweet, users: Set[User]) =
    Future.traverse(users) { user =>
      client.lpush(c.userWall(user), rweet)
    } map { _ => () }

  private def sendToTags(rweet: Rweet, tags: Set[HashTag]) =
    Future.traverse(tags) { tag =>
      client.lpush(c.hashWall(tag), rweet)
    } map { _ => () }

}
