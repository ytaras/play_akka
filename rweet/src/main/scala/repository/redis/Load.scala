package rweet.repository.redis

import rweet.{Redis, Repository}
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait Load extends Redis with Repository {

  def c: WallCollections

  trait WallCollections {
    def userWall(u: User): String
    def hashWall(t: HashTag): String
  }

  def userWall(of: User) =
    client.lrange[Rweet](c.userWall(of), 0, 100)
  def hashTags(tag: HashTag) =
    client.lrange[Rweet](c.hashWall(tag), 0, 100)

}
