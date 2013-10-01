package rweet

import org.scalatest._
import spray.testkit._
import spray.httpx.SprayJsonSupport._

class ServerSpec extends FunSpec with Matchers
    with ScalatestRouteTest {
  object sut extends DemoService {
    val actorRefFactory = system
  }
  import sut._

  describe("GET /hello") {
    it("returns 'World'") {
      Get("/hello") ~> route ~> check {
        entityAs[String] === "World"
      }
    }
  }

  describe("GET /users/:user") {
    it("returns user description") {
      Get("/users/user1") ~> route ~> check {
        entityAs[UserGodObject] === UserGodObject("user1")
      }
    }
  }

}
