package utils

import actors.PersistenceActorDispatcher
import actors.PersistenceActorDispatcher.RestartActors
import actors.PersistenceActorDispatcher.StartActors
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import javax.inject.Inject
import javax.inject.Singleton
import models.PersistenceMap

@Singleton
class DispatcherHelper @Inject() (system: ActorSystem) extends DispatcherHelperBase[PersistenceMap] {
  
  private lazy val dispatcher = system.actorOf(PersistenceActorDispatcher.props)
  
  override def dispatcherActor = dispatcher
  
  def start(items: List[PersistenceMap]) = {
    dispatcherActor ! StartActors(items)
  }
  
  def restart(items: List[PersistenceMap]) = {
    dispatcherActor ! RestartActors(items)
  }
}

