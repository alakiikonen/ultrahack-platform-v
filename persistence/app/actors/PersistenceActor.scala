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

import scala.collection.JavaConversions._

import java.util.Properties

import models._

object PersistenceActor {
  case class StartWritingToDb()
  case class StopWriting()

  /*val kafkaProps = new Properties()
  kafkaProps.put("group.id", "test")
  kafkaProps.put("zookeeper.connect", "localhost:2181")
  kafkaProps.put("host", "localhost")
  kafkaProps.put("port", "2181")
  kafkaProps.put("timeOut", "3000")
  kafkaProps.put("bufferSize", "100")
  kafkaProps.put("clientId", "platformv")

  protected val keyDecoder: Decoder[Array[Byte]] = new DefaultDecoder()
  protected val valueDecoder: Decoder[Array[Byte]] = new DefaultDecoder()

  val config = new ConsumerConfig(kafkaProps)*/
}

class PersistenceActor(system: ActorSystem, id: String, databaseTable: String, kafkaTopic: String) extends Actor {
  import PersistenceActor._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
  
  //private val system = ActorSystem("writeToDB")
  
  private val consumer = SingleTopicConsumer("sensor1")

  private val sensorDataDao = current.injector.instanceOf[SensorDataDao]

  //val filterSpec = new Whitelist(kafkaTopic)
  //lazy val stream = consumer.createMessageStreamsByFilter(filterSpec, 1, keyDecoder, valueDecoder).get(0)
  //def read(): Stream[String] = Stream.cons(new String(stream.head.message()), read())

  self ! StartWritingToDb

  def receive = {
    case StartWritingToDb => {
      println(s"start read: topic: $kafkaTopic, databaseTable: $databaseTable, id: $id")
      consumer.read().foreach { s =>
        sensorDataDao.insert(id, databaseTable, kafkaTopic, Json.parse(s).as[JsObject])
      }
    }
  }
}

@Singleton
class PersistenceDispatcherActor @Inject() (system: ActorSystem) extends Actor {
  import scala.concurrent.duration._
  import PersistenceDispatcherActor._
  import PersistenceActor._

  var writerActors = Map[ActorRef, String]()

  def receive = {
    case StartWriting(id: String, databaseTable: String, kafkaTopic: String) => {
      println(s"Start writing topic $kafkaTopic to database ta ble $databaseTable")
      writerActors = tryRemoveWriter(writerActors, id)
      val writeActor = context.actorOf(Props(new PersistenceActor(system, id, databaseTable, kafkaTopic)))
      writerActors += Tuple2(writeActor, id)
    }
    case StopWritingId(id: String) => {
      println(s"stop writing id $id")
      writerActors = tryRemoveWriter(writerActors, id)
    }
    case StopAllWriting => {
      println("stop polling")
      writerActors foreach { a =>
        a._1 ! PoisonPill
      }
      writerActors = writerActors.empty
    }
  }
}

object PersistenceDispatcherActor {
  def props = Props[PersistenceDispatcherActor]
  case class StartWriting(id: String, databaseTable: String, kafkaTopic: String)
  case class StopWritingId(id: String)
  case class StopAllWriting()

  def tryRemoveWriter(writerActors: Map[ActorRef, String], id: String) = {
    writerActors.find(p => p._2 == id).map { a =>
      a._1 ! PoisonPill
      writerActors - a._1
    }.getOrElse(writerActors)
  }
}