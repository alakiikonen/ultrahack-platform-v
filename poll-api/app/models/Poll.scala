package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._
import fi.platformv.models.Editable

import fi.platformv.actors.SpawnActorPropsBase

case class Poll(
  _id: Option[BSONObjectID],
  id: String,
  api: String,
  interval: Long,
  kafkaTopic: String,
  active: Boolean,
  created: Option[DateTime],
  changed: Option[DateTime]) extends SpawnActorPropsBase with Editable

object Poll {
  implicit val pollFormat = Json.format[Poll]
}

case class PollStatus(
  api: String,
  total: Long,
  success: Long,
  lastWasSuccess: Boolean)
  
object PollStatus {
  implicit val pollStatusFormat = Json.format[PollStatus]
}
