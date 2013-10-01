package rweet

import org.scalatest._
import spray.testkit._
import spray.http.MediaTypes._
import spray.http.HttpEntity
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling.{Unmarshaller, pimpHttpEntity}
import org.scalatest.concurrent._
import org.scalatest.concurrent.ScalaFutures._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json.DefaultJsonProtocol._

class ServerSpec extends FunSpec with Matchers
    with ScalatestRouteTest with BeforeAndAfter {
  object sut extends DemoService {
    val actorRefFactory = system
  }

  import sut._

  before {
    import aaa._
    aaa.client.flushdb.futureValue
  }
  describe("REST API") {
    describe("GET /hello") {
      it("returns 'World'") {
        Get("/hello") ~> route ~> check {
          entityAs[String] should === ("World")
        }
      }
    }

    describe("GET /users/:user") {
      it("returns user description") {
        Get("/users/user1") ~> route ~> check {
          entityAs[UserGodObject] should === (UserGodObject("user1"))
        }
      }
    }

    describe("GET /users/:user/followers") {
      it("returns list of user followers") {
        import aaa.{follow, followers, User}
        val bob = User("bob")
        val alice = User("alice")
        follow(bob, alice).futureValue
        followers(alice).futureValue should === (Set(bob))
        Get("/users/alice/followers") ~> route ~> check {
          import aaa._
          entityAs[List[User]] should === (List(bob))
        }
      }
    }
  }
}
