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

import models._
import utils.DispatcherHelper

@Singleton
class PersistenceDao @Inject() (val reactiveMongoApi: ReactiveMongoApi, dispatcherHelper: DispatcherHelper) {

  private def collection = reactiveMongoApi.db.collection[JSONCollection]("persistence")
  
  def ensureIndexes() = {
    import reactivemongo.api.indexes.{ Index, IndexType}
    val idIndex = new Index(Seq(("id", IndexType.Ascending)), None, true)
    collection.indexesManager.ensure(idIndex)
  }
  ensureIndexes()

  def save(persistenceMap: PersistenceMap) = {
    val p = persistenceMap.copy(
      _id = Some(persistenceMap._id.getOrElse(BSONObjectID.generate)),
      changed = Some(DateTime.now),
      created = Some(persistenceMap.created.getOrElse((DateTime.now))))
    val selector = Json.obj("_id" -> p._id.get)
    collection.update(selector, p, upsert = true).map { updateWriteResult =>
      if (updateWriteResult.ok) dispatcherHelper.start(List(p))
      updateWriteResult
    }
  }

  def findAll = {
    collection.find(Json.obj())
      .cursor[PersistenceMap]()
      .collect[List]()
  }

  def remove(id: String) = {
    val selector = Json.obj("id" -> id)
    collection.remove(selector).map { updateWriteResult =>
      if (updateWriteResult.ok) dispatcherHelper.stopById(id)
      updateWriteResult
    }
  }
}
