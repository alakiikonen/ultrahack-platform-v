package actors

import play.api.mvc._
import akka.actor._
import javax.inject._
import kafka.consumer.{ SimpleConsumer, ConsumerConfig, Consumer => KafkaConsumer, ConsumerIterator, Whitelist }
import kafka.serializer.{ DefaultDecoder, Decoder }
import java.util.HashMap
import kafka.serializer.StringDecoder
import play.api.libs.ws._
import play.api.Play.current
import dao.SensorDataDao
import play.api.libs.json._
import scala.util.Try
import scala.concurrent.Future
import DispatcherActor._
import scala.concurrent.duration._

import scala.collection.JavaConversions._

import java.util.Properties

import models._

object DispatcherActor {
  case class Start(persistenceMaps: List[PersistenceMap])
  case class Restart(persistenceMaps: List[PersistenceMap])
  case class Stop(id: String)
  case class StopAll()
}

@Singleton
class DispatcherActor @Inject() (system: ActorSystem) extends Actor {

  def receive = {

    case DispatcherActor.Start(persistenceMaps: List[PersistenceMap]) => {
      //println(s"Start writing topic $kafkaTopic to database table $databaseTable with id: $id")
      persistenceMaps foreach { persistenceMap =>
        if (persistenceMap._id.isDefined) {
          stopById(persistenceMap._id.get.stringify)
          val actor = context.actorOf(Props(new ApiPersistenceActor(system, persistenceMap)))
        }
      }
    }

    case DispatcherActor.Restart(persistenceMaps: List[PersistenceMap]) => {
      stopAll
      self ! Start(persistenceMaps)
    }

    case DispatcherActor.Stop(id: String) => {
      context.children.foreach { childActor =>
        childActor ! DispatcherActor.Stop(id)
      }
    }

    case DispatcherActor.StopAll => {
      println("stop writing")
      context.children.foreach { a =>
        a ! DispatcherActor.StopAll
      }
    }
  }

  private def stopAll {
    println("stop polling")
    context.children foreach { child =>
      child ! DispatcherActor.Stop
    }
  }

  private def stopById(id: String) {
    context.children.foreach { actor =>
      actor ! DispatcherActor.Stop(id)
    }
  }
}

