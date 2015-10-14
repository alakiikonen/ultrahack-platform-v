package models

import play.api.libs.json._
import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._
import fi.platformv.models.Editable

case class PersistenceMap(
  _id: Option[BSONObjectID],
  id: String,
  database: String,
  topic: String,
  batchSize: Int,
  created: Option[DateTime],
  changed: Option[DateTime]) extends Editable

object PersistenceMap {
  implicit val persistenceMapFormat = Json.format[PersistenceMap]
}