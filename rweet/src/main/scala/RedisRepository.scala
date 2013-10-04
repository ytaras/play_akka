package rweet

import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._
import scala.concurrent.Future
import rweet.repository.redis._

trait RedisRepository extends Redis with Repository with Follow
    with Send with Load {
  /**
    * Collection names
    **/
  val c = new WallCollections with FollowCollections {
    def followers(u: User) = s"user:${u.id}:followers"
    def followed(u: User) = s"user:${u.id}:followed"
    def userWall(u: User) = s"user.${u.id}.wall"
    def hashWall(t: HashTag) = s"tag.${t.tag}.wall"
  }
}
