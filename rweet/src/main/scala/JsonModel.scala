package rweet

import spray.json.DefaultJsonProtocol._

trait JsonModel extends Model {
  implicit val userFormat = jsonFormat1(User)
  implicit val hashTagFormat = jsonFormat1(HashTag)
  implicit val rweetFormat = jsonFormat4(Rweet.apply)
}
