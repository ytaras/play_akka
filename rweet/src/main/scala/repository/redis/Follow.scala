package rweet.repository.redis
import rweet.{Redis, Repository}
import com.redis.serialization.SprayJsonSupport._
import com.redis.serialization._

trait Follow extends Redis with Repository {
  def c: FollowCollections

  trait FollowCollections {
    def followers(u: User): String
    def followed(u: User): String
  }

  def follow(by: User, of: User) =
    client.sadd(c.followers(of), by) zip client
      .sadd(c.followed(by), of) map { _ => () }

  def followers(of: User) =
    client.smembers[User](c.followers(of))
  def followed(by: User) =
    client.smembers[User](c.followed(by))

}
