package actors

import play.api.mvc._
import akka.actor._
import javax.inject._
import org.apache.kafka.clients.producer.{ ProducerConfig, KafkaProducer, ProducerRecord }
import java.util.HashMap
import kafka.serializer.StringDecoder
import play.api.libs.ws._
import models._
import org.joda.time.DateTime
import fi.platformv.KafkaConstants
import play.api.libs.json._

import fi.platformv.actors.SpawnActorBase
import fi.platformv.actors.DispatcherActorBase.{ StopActors, StopActor, GetStatus }
import fi.platformv.models.ActorStatus

import java.util.concurrent.TimeoutException

object PollerActor {
  case class PollApi()

  val kafkaProps = new HashMap[String, Object]()
  kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
}

class PollerActor(system: ActorSystem, ws: WSClient, poll: Poll) extends SpawnActorBase(poll) {
  import PollerActor._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.util.Random

  private val rnd = new Random(DateTime.now.getMillis)

  var total: Long = 0
  var success: Long = 0
  var lastWasSuccess: Boolean = false

  val producer = new KafkaProducer[String, String](kafkaProps)
  val cancellable = system.scheduler.schedule((rnd.nextInt(1000)).milliseconds, poll.interval.milliseconds, self, PollApi)

  def receive = {
    case PollApi => {
      total += 1
      ws.url(poll.api).withRequestTimeout(poll.interval - 1).get().map { response =>
        if (Set(200, 201, 202, 203, 204, 205, 206).contains(response.status)) {
          success += 1
          lastWasSuccess = true
          val message = new ProducerRecord[String, String](poll.kafkaTopic, poll._id.get.stringify, response.body)
          producer.send(message)
        } else {
          lastWasSuccess = false
          val errorMessage = Json.obj("errorType" -> "invalidStatus", "time" -> DateTime.now, "poll" -> poll, "response" -> Json.obj("status" -> response.status, "body" -> response.body))
          val message = new ProducerRecord[String, String](KafkaConstants.POLL_ERROR_TOPIC, poll._id.get.stringify, Json.stringify(errorMessage))
          producer.send(message)
        }
      } recover {
        case t: TimeoutException => {
          val errorMessage = Json.obj("errorType" -> "timeout", "time" -> DateTime.now, "poll" -> poll, "msg" -> t.getMessage)
          val message = new ProducerRecord[String, String](KafkaConstants.POLL_ERROR_TOPIC, poll._id.get.stringify, Json.stringify(errorMessage))
          producer.send(message)
        }
        case e =>
          val errorMessage = Json.obj("errorType" -> "unknown", "time" -> DateTime.now, "poll" -> poll, "msg" -> e.getMessage)
          val message = new ProducerRecord[String, String](KafkaConstants.POLL_ERROR_TOPIC, poll._id.get.stringify, Json.stringify(errorMessage))
          producer.send(message)
      }
    }

    case StopActor(id: String) => {
      if (id == poll._id.get.stringify) {
        println(s"Stop polling from api ${poll.api}")
        cancellable.cancel()
        context stop self
      }
    }

    case StopActors => {
      println(s"Stop polling from api ${poll.api}")
      cancellable.cancel()
      context stop self
    }

    case GetStatus => {
      sender() ! ActorStatus(poll.id, poll.active, !lastWasSuccess, success, total, Json.obj("api" -> poll.api))
    }
  }
}
