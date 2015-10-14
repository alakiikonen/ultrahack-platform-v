package actors

import play.api.mvc._
import akka.actor._
import javax.inject._
import org.apache.kafka.clients.producer.{ ProducerConfig, KafkaProducer, ProducerRecord }
import java.util.HashMap
import kafka.serializer.StringDecoder
import play.api.libs.ws._
import models._
import actors._
import akka.pattern.ask
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

@Singleton
class DispatcherActor @Inject() (system: ActorSystem, ws: WSClient) extends Actor {
  import scala.concurrent.duration._
  import DispatcherActor._
  import PollerActor._

  var pollerActors = Map[ActorRef, Poll]()

  def receive = {
    case RestartPolling(polls: List[Poll]) => {
      println(s"Start polling")
      stopAll
      polls foreach { poll =>
        if (poll._id.isDefined && poll.active) {
          val pollActor = context.actorOf(Props(new PollerActor(system, ws, poll)))
          pollerActors += Tuple2(pollActor, poll)
        }
      }
    }
    case StopPollingId(id: String) => {
      println(s"stop polling id $id")
      pollerActors = tryRemovePoller(pollerActors, id)
    }
    
    case StopPollingAll => {
      stopAll
    }
    
    case GetStatuses => {
      sender() ! Future.sequence(pollerActors.map { actor => 
        implicit val timeout: akka.util.Timeout = 3.seconds
        (actor._1 ? GetStatus).mapTo[PollStatus]
      })
    }
    
  }

  private def stopAll = {
    println("stop polling")
    pollerActors foreach { a =>
      a._1 ! PollerActor.StopPolling
    }
    pollerActors = pollerActors.empty
  }
}

object DispatcherActor {
  case class RestartPolling(polls: List[Poll])
  case class StopPollingId(id: String)
  case class StopPollingAll()
  case class GetStatuses()

  def tryRemovePoller(pollerActors: Map[ActorRef, Poll], id: String) = {
    pollerActors.find(p => p._2._id.get.stringify == id).map { a =>
      a._1 ! PollerActor.StopPolling
      pollerActors - a._1
    }.getOrElse(pollerActors)
  }
}