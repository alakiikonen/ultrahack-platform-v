package controllers

import scala.concurrent.Future.{ successful => resolve }

import dao.PollDao
import javax.inject.Inject
import javax.inject.Singleton
import models.Poll
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import reactivemongo.bson.BSONObjectID
import utils.DispatcherHelper

@Singleton
class Application @Inject() (pollDao: PollDao, dispatcherHelper: DispatcherHelper) extends Controller {

  def index = Action {
    Ok("index")
  }

  def status = Action.async(parse.empty) { request =>
    dispatcherHelper.getStatus map { statuses =>
      Ok(Json.toJson(statuses))
    }
  }

  def start = Action.async(parse.empty) { request =>
    pollDao.findAll map { polls =>
      dispatcherHelper.start(polls)
      Ok("started")
    }
  }

  def restart = Action.async(parse.empty) { request =>
    pollDao.findAll map { polls =>
      dispatcherHelper.restart(polls)
      Ok("restarted")
    }
  }

  def stopAll = Action {
    dispatcherHelper.stopAll
    Ok("stopped all")
  }

  def savePoll = Action.async(parse.json) { request =>
    request.body.validate[Poll].map {
      case (poll) => {
        pollDao.save(poll).map {
          case updateWriteResult if updateWriteResult.ok => Ok("Done")
          case updateWriteResult                         => InternalServerError(s"Fail")
        }
      }
    } recoverTotal { e =>
      resolve(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(e))))
    }
  }

  def deletePoll(id: String) = Action.async(parse.empty) { request =>
    pollDao.remove(id).map {
      case updateWriteResult if updateWriteResult.ok => Ok("Done")
      case updateWriteResult                         => InternalServerError(s"Fail")
    }
  }

  def findAllPolls = Action.async(parse.empty) { request =>
    pollDao.findAll.map { polls =>
      Ok(Json.toJson(polls))
    }
  }

}


