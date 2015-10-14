package dao

import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import org.joda.time.DateTime

import javax.inject.Inject
import javax.inject.Singleton

import models.Poll

class PollDao @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

  private def collection = reactiveMongoApi.db.collection[JSONCollection]("polls")

  def save(poll: Poll) = {
    val p = poll.copy(
      _id = Some(poll._id.getOrElse(BSONObjectID.generate)),
      changed = Some(DateTime.now),
      created = Some(poll.created.getOrElse((DateTime.now))))
    val selector = Json.obj("_id" -> p._id.get)
    collection.update(selector, p, upsert = true)
  }

  def findAll = {
    collection.find(Json.obj())
      .cursor[Poll]()
      .collect[List]()
  }

  def remove(id: BSONObjectID) = {
    val selector = Json.obj("_id" -> id)
    collection.remove(selector)
  }
}
