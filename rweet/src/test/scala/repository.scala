package rweet

import org.specs2.mutable._
import org.specs2.specification.BeforeExample
import org.scalatest.concurrent.Futures._

class RepositorySpec extends Specification with Scope {
  implicit val followUser = new Before {
    // TODO Race condition is possible because we're not blocking here
    def before =
      a.followUser(users(1), users(2))
  }
  "The UserFollow API" should {
    "follow user" should  {
      "add user to followers" in
        a.followers(users(2)).futureValue.should == Set(users(2))
      "add user to followed" in
        a.followed(users(2)).futureValue.should == Set(users(1))
    }
  }

  "Send Rweet API" should {
    "send rweet" should {
      // TODO How to stack before's?
      implicit val sendRweet = new Before {
        val text = "Hello, @user4, we're talking about #stuff"
        a.sendRweet(rweetOf(users(2), text))
      }
      "send to author wall" in
        a.userWall(users(2)).map { _.length }.futureValue === 1
      "send to follower's wall" in
        a.userWall(users(1)).map { _.length }.futureValue === 1
      "send to mentioned user's wall" in
        a.userWall(users(4)).map { _.length }.futureValue === 1
      "send to hashTag's wall" in
        a.hashTags(HashTag("stuff")).map { _.length }.futureValue === 1
      "don't sent to anybody else" in
        Future.sequence(
          // TODO Should be just drop 1 and 2 instead
          (2 to 10).map { users(_) }
            .map { a.userWall(_) }.map { _.isEmpty }
        ).futureValue.should haveAll { _ }
      "don't send to other tags" in
        a.hashTags(HashTag("otherStuff")).map { _.isEmpty }.futureValue
          .should be_true
    }

  }
}

trait Scope extends BeforeExample {
  object a extends api with model with persistence {
    def cleanDb = client.flushdb()
  }
  val users = for {
    i <- 1 to 10
  } yield a.User(s"user${i}")
  def before = a.cleanDb
}
