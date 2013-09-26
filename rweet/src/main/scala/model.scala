package rweet

trait model {
  case class User(username: String)
  case class HashTag(tag: String)
  case class Rweet(content: String, users: List[User], tags: List[HashTag])
}
