package models

import org.joda.time.DateTime

import fi.platformv.models.SpawnActorPropsBase
import fi.platformv.models.Editable
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat


case class PersistenceMap(
  _id: Option[BSONObjectID],
  id: String,
  database: String,
  topic: String,
  batchSize: Int,
  active: Boolean,
  created: Option[DateTime],
  changed: Option[DateTime]) extends SpawnActorPropsBase with Editable

object PersistenceMap {
  implicit val persistenceMapFormat = Json.format[PersistenceMap]
}