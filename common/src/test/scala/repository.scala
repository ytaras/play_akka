package rweet

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{Span, Millis, Seconds}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RepositorySpec extends FunSpec with Matchers
    with GivenWhenThen with Repository {
  implicit val patienceConfig =
    PatienceConfig(Span(1, Seconds), Span(15, Millis))
  describe("Repository") {
    describe("Follow API") {
      it("persists follow link") {
        Given("empty db")
        When("user1 follows user2")
        follow(sampleUsers(1), sampleUsers(2)).futureValue

        Then("user1 should appear in followers of user2")
        followers(sampleUsers(2)).futureValue should === (Set(sampleUsers(1)))

        Then("user2 should appear in followed of user1")
        followed(sampleUsers(1)).futureValue should === (Set(sampleUsers(2)))
      }
    }

    describe("Posting Rweet") {
      it("posts a tweet") {
        Given("user1 follows user2")
        follow(sampleUsers(1), sampleUsers(2)).futureValue

        When("send rweet")
        val rweet = Rweet.parse("Hello, @user3, we're talking about #stuff",
          sampleUsers(2))
        sendRweet(rweet).futureValue

        Then("rweet is sent to author's wall")
        userWall(sampleUsers(2)).futureValue should === (List(rweet))
        Then("rweet is sent to follower's wall")
        userWall(sampleUsers(1)).futureValue should === (List(rweet))
        Then("rweet is sent to mentioned user's wall")
        userWall(sampleUsers(3)).futureValue should === (List(rweet))
        Then("rweet is sent to hash collection")
        hashTags(HashTag("stuff")).futureValue should === (List(rweet))
        Then("rweet is not sent to other users")
          (4 to 10).map {
            i => userWall(sampleUsers(i)).futureValue.isEmpty
          } should contain only(true)
        Then("rweet is not sent to other hash tags")
        List("other", "tag").map {
          i => hashTags(HashTag(i)).futureValue.isEmpty
        } should contain only(true)
      }
    }
  }

  val sampleUsers: Map[Int, User] =
    (1 to 10).map { i => (i, User(s"user${i}"))}.toMap
}
