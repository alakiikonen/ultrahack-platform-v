package utils

import actors.PollActorDispatcher
import actors.PollActorDispatcher.RestartActors
import actors.PollActorDispatcher.StartActors
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import javax.inject.Inject
import javax.inject.Singleton
import models.Poll

@Singleton
class DispatcherHelper @Inject() (system: ActorSystem) extends DispatcherHelperBase[Poll] {
  
  private lazy val dispatcher = system.actorOf(PollActorDispatcher.props)
  
  override def dispatcherActor = dispatcher
  
  def start(items: List[Poll]) = {
    dispatcherActor ! StartActors(items)
  }
  
  def restart(items: List[Poll]) = {
    dispatcherActor ! RestartActors(items)
  }
}

