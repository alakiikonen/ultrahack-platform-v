package actors

import actors.PersistenceActorDispatcher.RestartActors
import actors.PersistenceActorDispatcher.StartActor
import actors.PersistenceActorDispatcher.StartActors
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import fi.platformv.actors.ActorDispatcher
import fi.platformv.actors.ActorDispatcher.GetActorStatuses
import fi.platformv.actors.ActorDispatcher.StopActorById
import fi.platformv.actors.ActorDispatcher.StopActors
import fi.platformv.models.KafkaErrorMessage
import models.PersistenceMap

class PersistenceActorDispatcher extends ActorDispatcher[PersistenceMap] {
  
  override def errorTopic = fi.platformv.utils.KafkaConstants.PERSISTENCE_ERROR_TOPIC

  def actorIdBase: String = PersistenceActorDispatcher.ACTOR_ID_BASE

  def spawnActor(context: ActorContext, props: PersistenceMap, uniqueName: String): ActorRef = {
    context.actorOf(Props(classOf[ApiPersistenceActor], props), uniqueName)
  }

  def receive = {

    case StartActors(items: List[PersistenceMap]) => {
      println("start actors")
      startActors(items)
    }

    case StartActor(item: PersistenceMap) => {
      println("start actor")
      startActor(item)
    }

    case RestartActors(items: List[PersistenceMap]) => {
      println("restart")
      restartWithActors(items)
    }

    case StopActorById(id: String) => {
      println("stop actor")
      stopActorById(id)
    }

    case StopActors => {
      println("stop actors")
      stopAllActors
    }

    case GetActorStatuses => {
      println("get statuses")
      sender() ! getStatuses
    }
    
    case err: KafkaErrorMessage => {
      sendErrorMessage(err)
    }
  }
}

object PersistenceActorDispatcher {
  case class StartActors(items: List[PersistenceMap])
  case class StartActor(item: PersistenceMap)
  case class RestartActors(items: List[PersistenceMap])
  
  final val ACTOR_ID_BASE = "persistence"
  
  val props = Props[PersistenceActorDispatcher]
}