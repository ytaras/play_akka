package rweet

trait model {
  case class User(id: String)
  case class HashTag(tag: String)
  case class Rweet(content: String, users: List[User],
    tags: List[HashTag], author: User)
}
