package controllers

import play.api._
import play.api.mvc._

import akka.actor._
import javax.inject._

import actors._
import play.api.Play.current
import utils.DispatcherHelper
import dao._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future.{ successful => resolve }
import models._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID

@Singleton
class Application @Inject() (system: ActorSystem, persistenceDao: PersistenceDao, dispatcherHelper: DispatcherHelper) extends Controller {

  def index = Action {
    Ok("index")
  }
  
  def status = Action.async(parse.empty) { request =>
    dispatcherHelper.getStatus map { statuses =>
      Ok(Json.toJson(statuses))
    }
  }

  def restart = Action.async(parse.empty) { request =>
    persistenceDao.findAll map { persistenceMaps =>
      dispatcherHelper.restart(persistenceMaps)
      Ok("restarted")
    }
  }

  def start = Action.async(parse.empty) { request =>
    persistenceDao.findAll map { persistenceMaps =>
      dispatcherHelper.start(persistenceMaps)
      Ok("started")
    }
  }

  def stopAll = Action {
    dispatcherHelper.stopAll
    Ok("stopping")
  }

  def savePersistenceMap = Action.async(parse.json) { request =>
    request.body.validate[PersistenceMap].map {
      case (persistenceMap) => {
        persistenceDao.save(persistenceMap).map {
          case updateWriteResult if updateWriteResult.ok => Ok("Done")
          case updateWriteResult                         => InternalServerError(s"Fail")
        }
      }
    } recoverTotal { e =>
      resolve(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(e))))
    }
  }

  def deletePersistenceMap(id: String) = Action.async(parse.empty) { request =>
    persistenceDao.remove(id).map {
      case updateWriteResult if updateWriteResult.ok => Ok("Done")
      case updateWriteResult                         => InternalServerError(s"Fail")
    }
  }

  def findAllPersistenceMaps = Action.async(parse.empty) { request =>
    persistenceDao.findAll.map { persistenceMap =>
      Ok(Json.toJson(persistenceMap))
    }
  }
}
