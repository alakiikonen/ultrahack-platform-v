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

object PollerActor {
  case class PollApi()
  case class StopPollingId(id: String)
  case class StopPolling()
  case class GetStatus()

  val kafkaProps = new HashMap[String, Object]()
  kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
}

class PollerActor(system: ActorSystem, ws: WSClient, val poll: Poll) extends Actor {
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
      ws.url(poll.api).withRequestTimeout(poll.interval).get().map { response =>
        if (Set(200, 201, 202, 203, 204, 205, 206).contains(response.status)) {
          success += 1
          lastWasSuccess = true
          val message = new ProducerRecord[String, String](poll.kafkaTopic, poll._id.get.stringify, response.body)
          producer.send(message)
        } else {
          lastWasSuccess = false
          val errorMessage = Json.obj("errorType" -> "poll-api-request-failed", "time" -> DateTime.now, "poll" -> poll, "response" -> Json.obj("status" -> response.status, "body" -> response.body))
          val message = new ProducerRecord[String, String](KafkaConstants.errorTopic, poll._id.get.stringify, Json.stringify(errorMessage))
          producer.send(message)
        }
      }
    }
    case StopPollingId(id: String) => {
      if (id == poll._id.get.stringify) {
        println(s"Stop polling from api ${poll.api}")
        cancellable.cancel()
        context stop self
      }
    }
    case StopPolling => {
      println(s"Stop polling from api ${poll.api}")
      cancellable.cancel()
      context stop self
    }
    case GetStatus => {
      sender() ! PollStatus(poll.api, success, total, lastWasSuccess)
    }
  }
}
