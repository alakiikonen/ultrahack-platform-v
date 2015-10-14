package actors

import play.api.mvc._
import akka.actor._
import javax.inject._
//import org.apache.kafka.clients.consumer.{ ConsumerConfig, KafkaConsumer, ConsumerRecord }
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

import scala.collection.JavaConversions._

import java.util.Properties

import models._
import ApiPersistenceActor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ApiPersistenceActor {
  case class WriteNextBatchToPersistentStorage(batchSize: Int)
}

class ApiPersistenceActor(system: ActorSystem, persistenceMap: PersistenceMap) extends Actor {
  println(s"start read: topic: ${persistenceMap.topic}, databaseTable: ${persistenceMap.database}, id: ${persistenceMap._id.get.stringify}")
  private val consumer = SingleTopicConsumer(persistenceMap.topic)
  
  private val sensorDataDao = current.injector.instanceOf[SensorDataDao]
  
  //private val consumer = ChunkConsumer(List("sensor1"))
  
  private var isShuttingDown = false
  private var total = 0
  private var success = 0
  
  self ! WriteNextBatchToPersistentStorage(persistenceMap.batchSize)

  def receive = {
    case WriteNextBatchToPersistentStorage(batchSize: Int) => {
      val batch = consumer.read().take(batchSize).toList
      Future.sequence {
         batch map { item =>
          sensorDataDao.insert(persistenceMap.id, persistenceMap.database, persistenceMap.topic, Json.parse(item).as[JsObject]).map { writeResult =>
            total += 1
            if(writeResult.ok && writeResult.n == 1) success += 1
            Unit
          }
        }
      } map { list =>
        println(s"saved batch to db ${persistenceMap.database}, total: $total, successes: $success")
        if (!isShuttingDown) self ! WriteNextBatchToPersistentStorage(batchSize)
        else {
          println(s"shut down persistence actor, id: ${persistenceMap.id}, processed: $total, successes: $success")
          consumer.shutdown()
          context stop self
        }
      }
    }
    
    case DispatcherActor.Stop(stopId: String) => {
      if(persistenceMap._id.get.stringify == stopId) self ! DispatcherActor.StopAll
    }
    
    case DispatcherActor.StopAll => {
      isShuttingDown = true
    }
  }
}
