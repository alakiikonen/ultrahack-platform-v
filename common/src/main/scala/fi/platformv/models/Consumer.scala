package fi.platformv.models

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerConfig

import fi.platformv.utils.KafkaConsumerConfig

abstract class ConsumerBase {
  
  protected val config: Properties = KafkaConsumerConfig()
  
  //protected lazy val consumer: KafkaConsumer[A, A] = new KafkaConsumer[A, A](config)

  def close(): Unit// = consumer.close()

  def read(): Iterable[String]
}
