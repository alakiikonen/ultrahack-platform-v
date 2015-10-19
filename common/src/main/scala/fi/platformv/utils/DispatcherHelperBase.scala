package utils

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout.durationToTimeout
import fi.platformv.actors.ActorDispatcher.GetActorStatuses
import fi.platformv.actors.ActorDispatcher.StopActorById
import fi.platformv.actors.ActorDispatcher.StopActors
import fi.platformv.models.ActorStatus
import fi.platformv.models.SpawnActorPropsBase
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait DispatcherHelperBase[PropsType <: SpawnActorPropsBase] {
  
  def dispatcherActor: ActorRef

  def getStatus: Future[List[ActorStatus]] = {
    implicit val timeout: akka.util.Timeout = 10.seconds
    (dispatcherActor ? GetActorStatuses).mapTo[Future[List[ActorStatus]]] flatMap{l => l}
  }
  
  def start(items: List[PropsType]): Unit
  
  def restart(items: List[PropsType]): Unit
  
  def stopAll = {
    dispatcherActor ! StopActors
  }
  
  def stopById(id: String) = {
    dispatcherActor ! StopActorById(id)
  }
}

