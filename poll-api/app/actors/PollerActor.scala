package actors

import java.util.concurrent.TimeoutException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.DurationLong
import scala.util.Random

import org.apache.kafka.clients.producer.ProducerRecord
import org.joda.time.DateTime

import PollerActor.PollApi
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import fi.platformv.actors.ActorDispatcher.GetActorStatus
import fi.platformv.models.ActorStatus
import fi.platformv.models.KafkaErrorMessage
import fi.platformv.models.KafkaErrorType
import fi.platformv.utils.KafkaConstants
import models.Poll
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.ws.WSClient

object PollerActor {
  case class PollApi()
  final val SOURCE_TYPE = "poller-actor"
}

class PollerActor(poll: Poll) extends Actor { 
  private val rnd = new Random(DateTime.now.getMillis)
  
  private val ws = current.injector.instanceOf[WSClient]

  private var total: Long = 0
  private var success: Long = 0
  private var lastWasSuccess: Boolean = false
  
  private val producer = fi.platformv.models.Producer[String](poll.kafkaTopic)
  
  private val cancellable = context.system.scheduler.schedule((rnd.nextInt(1000)).milliseconds, poll.interval.milliseconds, self, PollApi)

  def receive = {
    case PollApi => {
      total += 1
      ws.url(poll.api).withRequestTimeout(poll.interval - 1).get().map { response =>
        if (Set(200, 201, 202, 203, 204, 205, 206).contains(response.status)) {
          success += 1
          lastWasSuccess = true
          val message = new ProducerRecord[String, String](poll.kafkaTopic, poll._id.get.stringify, response.body)
          producer.sendWithKey(response.body, poll._id.get.stringify)
        } else {
          lastWasSuccess = false
          context.parent ! KafkaErrorMessage(KafkaErrorType.INVALID_RESPONSE_STATUS, response.status.toString, PollerActor.SOURCE_TYPE, poll.id, DateTime.now, Some(Json.obj("body" -> response.body)))
        }
      } recover {
        case t: TimeoutException => {
          lastWasSuccess = false
          context.parent ! KafkaErrorMessage(KafkaErrorType.TIMEOUT, t.getMessage, PollerActor.SOURCE_TYPE, poll.id, DateTime.now, None)
        }
        case e => {
          lastWasSuccess = false
          context.parent ! KafkaErrorMessage(KafkaErrorType.SERVICE_UNAVAILABLE, e.getMessage, PollerActor.SOURCE_TYPE, poll.id, DateTime.now, None)
        }
      }
    }

    case GetActorStatus => {
      sender() ! ActorStatus(poll.id, poll.active, !lastWasSuccess, success, total, Json.obj("api" -> poll.api, "topic" -> poll.kafkaTopic))
    }
  }

  override def postStop(): Unit = {
    println(s"stop actor ${poll.id}")
    cancellable.cancel()
    producer.close()
  }
}
