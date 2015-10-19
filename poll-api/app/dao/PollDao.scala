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
import utils.DispatcherHelper

@Singleton
class PollDao @Inject() (val reactiveMongoApi: ReactiveMongoApi, dispatcherHelper: DispatcherHelper) {

  private def collection = reactiveMongoApi.db.collection[JSONCollection]("polls")
  
  def ensureIndexes() = {
    import reactivemongo.api.indexes.{ Index, IndexType}
    val idIndex = new Index(Seq(("id", IndexType.Ascending)), None, true)
    collection.indexesManager.ensure(idIndex)
  }
  ensureIndexes()

  def save(poll: Poll) = {
    val p = poll.copy(
      _id = Some(poll._id.getOrElse(BSONObjectID.generate)),
      changed = Some(DateTime.now),
      created = Some(poll.created.getOrElse((DateTime.now))))
    val selector = Json.obj("_id" -> p._id.get)
    collection.update(selector, p, upsert = true).map { updateWriteResult =>
      if (updateWriteResult.ok) dispatcherHelper.start(List(p))
      updateWriteResult
    }
  }

  def findAll = {
    collection.find(Json.obj())
      .cursor[Poll]()
      .collect[List]()
  }

  def remove(id: String) = {
    val selector = Json.obj("id" -> id)
    collection.remove(selector, firstMatchOnly = true).map { updateWriteResult =>
      if (updateWriteResult.ok) dispatcherHelper.stopById(id)
      updateWriteResult
    }
  }
}
