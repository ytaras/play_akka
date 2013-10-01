package rweet

import org.scalatest._
import org.scalautils.StringNormalizations._
import org.scalatest.concurrent._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{Span, Millis, Seconds}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepositorySpec extends FunSpec with Matchers
    with GivenWhenThen with BeforeAndAfter {
  object sut extends api with model with persistence
  import sut._
  implicit val patienceConfig =
    PatienceConfig(Span(1, Seconds), Span(15, Millis))
  before {
    client.flushdb.futureValue
  }
  describe("Repository") {
    describe("Follow API") {
      it("persists follow link") {
        Given("empty db")
        When("user1 follows user2")
        follow(users(1), users(2)).futureValue

        Then("user1 should appear in followers of user2")
        followers(users(2)).futureValue should === (Set(users(1)))

        Then("user2 should appear in followed of user1")
        followed(users(1)).futureValue should === (Set(users(2)))
      }
    }

    describe("Posting Rweet") {
      it("posts a tweet") {
        Given("user1 follows user2")
        follow(users(1), users(2)).futureValue

        When("send rweet")
        val rweet = Rweet.parse("Hello, @user3, we're talking about #stuff",
          users(2))
        sendRweet(rweet).futureValue

        Then("rweet is sent to author's wall")
        userWall(users(2)).futureValue should === (List(rweet))
        Then("rweet is sent to follower's wall")
        userWall(users(1)).futureValue should === (List(rweet))
        Then("rweet is sent to mentioned user's wall")
        userWall(users(3)).futureValue should === (List(rweet))
        Then("rweet is sent to hash collection")
        hashTags(HashTag("stuff")).futureValue should === (List(rweet))
        Then("rweet is not sent to other users")
          (4 to 10).map {
            i => userWall(users(i)).futureValue.isEmpty
          } should contain only(true)
        Then("rweet is not sent to other hash tags")
        List("other", "tag").map {
          i => hashTags(HashTag(i)).futureValue.isEmpty
        } should contain only(true)

      }
    }
  }

  val users: Map[Int, User] =
    (1 to 10).map { i => (i, User(s"user${i}"))}.toMap
}
