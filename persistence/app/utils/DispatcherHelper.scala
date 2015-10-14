package utils

import javax.inject._
import actors._
import actors.DispatcherActor
import akka.actor._
import play.api.Play.current
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models._

@Singleton
class DispatcherHelper @Inject() (system: ActorSystem) {

  private val dispatcherActor = system.actorOf(Props(current.injector.instanceOf[DispatcherActor]), "dispatcher-actor")

  /*def getStatus: Future[List[PollStatus]] = {
    implicit val timeout: akka.util.Timeout = 5.seconds
    (dispatcherActor ? DispatcherActor.GetStatuses).mapTo[Future[List[PollStatus]]] flatMap(l => l)
  }*/

  def startWriting(persistenceMaps: List[PersistenceMap]) = {
    dispatcherActor ! DispatcherActor.Start(persistenceMaps)
  }
  
  def restartWriting(persistenceMaps: List[PersistenceMap]) = {
    dispatcherActor ! DispatcherActor.Restart(persistenceMaps)
  }

  def stopAll = {
    dispatcherActor ! DispatcherActor.StopAll
  }

  def stopById(id: String) = {
    dispatcherActor ! DispatcherActor.Stop(id)
  }
}