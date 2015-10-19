package actors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.joda.time.DateTime

import ApiPersistenceActor.WriteNextBatchToPersistentStorage
import akka.actor.Actor
import akka.actor.actorRef2Scala
import dao.SensorDataDao
import fi.platformv.actors.ActorDispatcher.GetActorStatus
import fi.platformv.actors.ActorDispatcher.StopActor
import fi.platformv.models.ActorStatus
import fi.platformv.models.KafkaErrorMessage
import fi.platformv.models.KafkaErrorType
import models.PersistenceMap
import models.SingleTopicConsumer
import play.api.Play.current
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper

object ApiPersistenceActor {
  case class WriteNextBatchToPersistentStorage(batchSize: Int)
  final val SOURCE_TYPE = "persistence-actor"
}

class ApiPersistenceActor(persistenceMap: PersistenceMap) extends Actor {
  println(s"start read: topic: ${persistenceMap.topic}, databaseTable: ${persistenceMap.database}, id: ${persistenceMap._id.get.stringify}")

  private val consumer = SingleTopicConsumer(persistenceMap.topic)

  private val sensorDataDao = current.injector.instanceOf[SensorDataDao]

  //private val consumer = ChunkConsumer(List("sensor1"))

  private var isShuttingDown = false
  private var total = 0
  private var success = 0
  private var lastWasSuccess: Boolean = false

  self ! WriteNextBatchToPersistentStorage(persistenceMap.batchSize)

  def receive = {
    case WriteNextBatchToPersistentStorage(batchSize: Int) => {
      val batch = consumer.read().take(batchSize).toList
      total += batchSize
      Future.sequence {
        batch map { item =>
          sensorDataDao.insert(persistenceMap.id, persistenceMap.database, persistenceMap.topic, Json.parse(item).as[JsObject]).map {
            case writeResult if writeResult.ok && writeResult.n == 1 => success += 1; Unit
            case writeResult if writeResult.ok => {
              context.parent ! KafkaErrorMessage(
                KafkaErrorType.DATABASE_WRITE_ERROR,
                s"invalid save count: ${writeResult.n}",
                ApiPersistenceActor.SOURCE_TYPE,
                persistenceMap.id,
                DateTime.now,
                None)
              Unit
            }
            case writeResult => {
              context.parent ! KafkaErrorMessage(
                KafkaErrorType.DATABASE_WRITE_ERROR,
                writeResult.errmsg.getOrElse("no error msg"),
                ApiPersistenceActor.SOURCE_TYPE,
                persistenceMap.id,
                DateTime.now,
                None)
              Unit
            }
          }
        }
      } map { list =>
        println(s"saved batch to db ${persistenceMap.database}, total: $total, successes: $success")
        lastWasSuccess = list.length == batchSize
        if (!isShuttingDown) self ! WriteNextBatchToPersistentStorage(batchSize)
        else {
          println(s"shut down persistence actor, id: ${persistenceMap.id}, processed: $total, successes: $success")
          context stop self
        }
      }
    }

    case StopActor => {
      isShuttingDown = true
    }

    case GetActorStatus => {
      sender() ! ActorStatus(persistenceMap.id, persistenceMap.active, !lastWasSuccess, success, total, Json.obj("db" -> persistenceMap.database, "topic" -> persistenceMap.topic))
    }
  }

  override def postStop(): Unit = {
    consumer.close()
  }
}
