package fi.platformv.actors

import javax.inject._
import akka.actor._

//import scala.reflect._
//import scala.reflect.api.TypeTags

import scala.reflect.ClassTag
import scala.reflect._

class DispatcherActorBase[SpawnActor: ClassTag, SpawnActorProps <: SpawnActorPropsBase](val spawn: (ActorContext, SpawnActorProps) => SpawnActor) extends Actor {
  case class StartActors(items: List[SpawnActorProps])
  case class StartActor(item: SpawnActorProps)
  case class RestartActors(items: List[SpawnActorProps])

  case class StopActor(id: String)
  case class StopActors()

  def receive = {

    case StartActors(items: List[SpawnActorProps]) => {
      items foreach { item =>
        stopByActorId(item.actorId)
        val actor = spawn(context, item)
      }
    }

    case StartActor(item: SpawnActorProps) => {
      stopByActorId(item.actorId)
      val actor = spawn(context, item)
    } 

    case RestartActors(items: List[SpawnActorProps]) => {
      stopAll
      self ! StartActors(items: List[SpawnActorProps])
    }

    case StopActor(id: String) => {
      context.children.foreach { childActor =>
        childActor ! StopActor(id)
      }
    }

    case StopActors => {
      context.children.foreach { childActor =>
        childActor ! StopActors
      }
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

}

abstract class SpawnActorBase @Inject() (system: ActorSystem)(item: SpawnActorPropsBase) extends Actor {

}

abstract class SpawnActorPropsBase {
  def actorId: String
}