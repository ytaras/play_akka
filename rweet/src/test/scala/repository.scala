package rweet

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.concurrent.ScalaFutures._

class RedisRepositorySpec extends RepositorySpec with RedisRepository
    with BeforeAndAfter {
  before {
    client.flushdb.futureValue
  }
}
