package utils

import javax.inject._
import actors._
import actors.PollDispatcherActor
import akka.actor._
import play.api.Play.current
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models._ 

import fi.platformv.actors.DispatcherActorBase._
import fi.platformv.models.ActorStatus

@Singleton
class DispatcherHelper @Inject() (system: ActorSystem) {
  
  private val pollDispatcherActor = system.actorOf(Props(current.injector.instanceOf[PollDispatcherActor]), "dispatcher-actor")
  
  def getStatus: Future[List[ActorStatus]] = {
    implicit val timeout: akka.util.Timeout = 10.seconds
    (pollDispatcherActor ? GetStatuses).mapTo[Future[List[ActorStatus]]] flatMap(l => l)
  }
  
  def startPolling(polls: List[Poll]) = {
    pollDispatcherActor ! StartActors(polls)
  }
  
  def restartPolling(polls: List[Poll]) = {
    pollDispatcherActor ! RestartActors(polls)
  }
  
  def stopAll = {
    pollDispatcherActor ! StopActors
  }
  
  def stopById(id: String) = {
    pollDispatcherActor ! StopActor(id)
  }
}
