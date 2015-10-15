package fi.platformv.models

import play.api.libs.json._

case class ActorStatus (
  id: String,
  active: Boolean,
  errors: Boolean,
  success: Long,
  total: Long,
  extra: JsObject
)

object ActorStatus {
  implicit val actorStatusFormat = Json.format[ActorStatus]
}