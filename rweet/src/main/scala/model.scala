package rweet

trait model {
  import parser._
  case class User(id: String)
  case class HashTag(tag: String)
  case class Rweet(content: String, users: Set[User],
    tags: Set[HashTag], author: User)

  object Rweet {
    def parse(m: String, a: User) =
      Rweet(m, users(m), hashTags(m), a)
  }
  def hashTags = parser.extract(hashReg, { HashTag(_) }) _
  def users = parser.extract(userReg, { User(_) }) _

  private object parser {
    import scala.util.matching._
    val hashReg = "#(\\w+)".r
    val userReg = "@(\\w+)".r
    def extract[A](r: Regex, m: String => A)(s: String) =
      r.findAllIn(s).matchData.map { _.group(1) }.map(m).toSet
  }

}
