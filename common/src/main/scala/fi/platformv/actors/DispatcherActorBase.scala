package fi.platformv.actors

import javax.inject._
import akka.actor._

import scala.reflect.ClassTag
import scala.reflect._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._

import fi.platformv.models.ActorStatus

class DispatcherActorBase[SpawnActor <: SpawnActorBase, SpawnActorProps <: SpawnActorPropsBase](val spawn: (ActorContext, SpawnActorProps) => ActorRef) extends Actor {
  import DispatcherActorBase._

  def receive = {

    case StartActors(items: List[SpawnActorProps]) => {
      println("start actors")
      items foreach { item =>
        stopByActorId(item.id)
        val actor = spawn(context, item)
      }
    }

    case StartActor(item: SpawnActorProps) => {
      println("start actor")
      stopByActorId(item.id)
      val actor = spawn(context, item)
    }

    case RestartActors(items: List[SpawnActorProps]) => {
      println("restart")
      stopAll
      self ! StartActors(items: List[SpawnActorProps])
    }

    case StopActor(id: String) => {
      println("stop actor")
      context.children.foreach { childActor =>
        childActor ! StopActor(id)
      }
    }

    case StopActors => {
      println("stop actors")
      context.children.foreach { childActor =>
        childActor ! StopActors
      }
    }
    
    case GetStatuses => {
      sender() ! Future.sequence(context.children.map { childActor =>
        implicit val timeout: akka.util.Timeout = 5.seconds
        (childActor ? GetStatus).mapTo[ActorStatus]
      })
    }
  }

  private def stopAll {
    context.children foreach { child =>
      child ! StopActors
    }
  }

  private def stopByActorId(actorId: String) {
    context.children.foreach { actor =>
      actor ! StopActor(actorId)
    }
  }

}

object DispatcherActorBase {
  case class StartActors(items: List[SpawnActorPropsBase])
  case class StartActor(item: SpawnActorPropsBase)
  
  case class RestartActors(items: List[SpawnActorPropsBase])

  case class StopActor(id: String)
  case class StopActors()
  
  case class GetStatuses()
  case class GetStatus()
}

abstract class SpawnActorBase(item: SpawnActorPropsBase) extends Actor

abstract class SpawnActorPropsBase {
  val id: String
}