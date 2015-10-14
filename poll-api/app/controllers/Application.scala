package controllers

import play.api._
import play.api.mvc._

import scala.concurrent.Future.{ successful => resolve }
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import akka.actor._
import javax.inject._
import actors._
import actors.DispatcherActor._
import play.api.Play.current
import models._
import dao._
import reactivemongo.bson.BSONObjectID
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Future

@Singleton
class Application @Inject() (system: ActorSystem, pollDao: PollDao) extends Controller {

  private val dispatcherActor = system.actorOf(Props(current.injector.instanceOf[DispatcherActor]), "dispatcher-actor")

  def index = Action {
    Ok("index")
  }

  def status = Action.async(parse.empty) { request =>
    implicit val timeout: akka.util.Timeout = 5.seconds
    (dispatcherActor ? GetStatuses).mapTo[Future[List[PollStatus]]] flatMap { statusesFuture =>
      statusesFuture.map { statuses => 
        Ok(Json.toJson(statuses))
      }
    }
  }

  def restart = Action.async(parse.empty) { request =>
    pollDao.findAll map { polls =>
      dispatcherActor ! RestartPolling(polls)
      Ok("restarted")
    }
  }

  def stopAll = Action {
    dispatcherActor ! StopPollingAll
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
    BSONObjectID.parse(id) map { id =>
      pollDao.remove(id).map {
        case updateWriteResult if updateWriteResult.ok => Ok("Done")
        case updateWriteResult                         => InternalServerError(s"Fail")
      }
    } getOrElse {
      resolve(BadRequest("invalid id"))
    }
  }

  def findAllPolls = Action.async(parse.empty) { request =>
    pollDao.findAll.map { polls =>
      Ok(Json.toJson(polls))
    }
  }

}


