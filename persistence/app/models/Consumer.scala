package models

//import example.utils.KafkaConfig
//import kafka.consumer.{ Consumer => KafkaConsumer }
import kafka.consumer.ConsumerConfig
//import kafka.serializer.{Decoder, DefaultDecoder}
//import scala.collection.JavaConversions._
//import kafka.api._
import java.util.Properties
import fi.platformv.KafkaConfig

abstract class Consumer(topics: List[String]) {

  //protected val kafkaConfig = KafkaConfig()
  //protected val config = new ConsumerConfig(kafkaConfig)

  /*val kafkaProps = new Properties()
  kafkaProps.put("group.id", "test")
  kafkaProps.put("zookeeper.connect", "localhost:2181")
  kafkaProps.put("host", "localhost")
  kafkaProps.put("port", "2181")
  kafkaProps.put("timeOut", "3000")
  kafkaProps.put("bufferSize", "100")
  kafkaProps.put("clientId", "platformv")*/
  val kafkaProps = KafkaConfig()
  val config = new ConsumerConfig(kafkaProps)

  def read(): Iterable[String]
}