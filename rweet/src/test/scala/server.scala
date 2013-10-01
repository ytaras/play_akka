package rweet

import org.scalatest._
import spray.testkit._

class ServerSpec extends FunSpec with Matchers
    with ScalatestRouteTest {
  object sut extends DemoService {
    val actorRefFactory = system
  }

  describe("GET /hello") {
    it("returns 'World'") {
      Get("/hello") ~> sut.demoRoute ~> check {
        entityAs[String] === "World"
      }
    }
  }

  describe("GET /users/:user") {
    it("returns user id") {
      Get("/users/user1") ~> sut.demoRoute ~> check {
        entityAs[String] === "user1"
      }
    }
  }

}
