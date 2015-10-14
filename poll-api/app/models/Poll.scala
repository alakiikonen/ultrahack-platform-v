package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._

trait Editable {
  def created: Option[DateTime]
  def changed: Option[DateTime]
}

case class Poll(
    _id: Option[BSONObjectID],
    api: String,
    interval: Long,
    kafkaTopic: String,
    active: Boolean,
    created: Option[DateTime],
    changed: Option[DateTime]
  ) extends Editable
  
case class PollStatus(
  api: String,
  total: Long,
  success: Long,
  lastWasSuccess: Boolean
)
object PollStatus {
  implicit val pollStatusFormat = Json.format[PollStatus]
}

object Poll {
  implicit val pollFormat = Json.format[Poll]
}


