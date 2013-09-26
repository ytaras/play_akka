package rweet

import scala.concurrent.Future

trait UserFollow { self: model =>
  def followUser(from: User, to: User): Future[Boolean]
  def followers(of: User): Future[List[User]]
  def followed(by: User): Future[List[User]]
}
