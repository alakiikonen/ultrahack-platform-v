package dao

import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson._

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import org.joda.time.DateTime

import javax.inject.Inject
import javax.inject.Singleton

class SensorDataDao @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

  private def collection(databaseTable: String) = reactiveMongoApi.db.collection[JSONCollection](databaseTable)

  def insert(id: String, databaseTable: String, kafkaTopic: String, json: JsObject) = {
    collection(databaseTable).insert(Json.obj(
      "sensorId" -> id,
      "topic" -> kafkaTopic,
      "reading" -> json))
  }
}
