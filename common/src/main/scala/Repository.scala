package rweet
import scala.concurrent.Future

trait Repository extends Model {
  def follow(by: User, of: User): Future[Unit]
  def followers(of: User): Future[Set[User]]
  def followed(by: User): Future[Set[User]]
  def sendRweet(rweet: Rweet): Future[Unit]
  def userWall(of: User): Future[List[Rweet]]
  def hashTags(tag: HashTag): Future[List[Rweet]]
}
