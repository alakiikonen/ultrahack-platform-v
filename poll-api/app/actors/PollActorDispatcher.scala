package actors

import akka.actor.ActorContext
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import fi.platformv.actors.ActorDispatcher
import fi.platformv.models.SpawnActorPropsBase
import fi.platformv.actors.ActorDispatcher._
import fi.platformv.models.KafkaErrorMessage
import javax.inject.Inject
import javax.inject.Singleton
import models.Poll
import play.api.libs.ws.WSClient
import actors.PollActorDispatcher._

class PollActorDispatcher extends ActorDispatcher[Poll] {
  
  override def errorTopic = fi.platformv.utils.KafkaConstants.POLL_ERROR_TOPIC

  def actorIdBase: String = PollActorDispatcher.ACTOR_ID_BASE

  def spawnActor(context: ActorContext, props: Poll, uniqueName: String): ActorRef = {
    context.actorOf(Props(classOf[PollerActor], props), uniqueName)
  }

  def receive = {

    case StartActors(items: List[Poll]) => {
      println("start actors")
      startActors(items)
    }

    case StartActor(item: Poll) => {
      println("start actor")
      startActor(item)
    }

    case RestartActors(items: List[Poll]) => {
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

object PollActorDispatcher {
  case class StartActors(items: List[Poll])
  case class StartActor(item: Poll)
  case class RestartActors(items: List[Poll])
  
  final val ACTOR_ID_BASE = "poller"
  
  val props = Props[PollActorDispatcher]
}
