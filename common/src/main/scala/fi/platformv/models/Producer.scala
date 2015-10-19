package fi.platformv.models

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

import fi.platformv.utils.KafkaProducerClientConfig

case class Producer[A](topic: String) {
  protected val config: Properties = KafkaProducerClientConfig()
  private lazy val producer: KafkaProducer[A, A] = new KafkaProducer[A, A](config)

  def send(message: A) = sendMessage(producer, producerRecord(topic, message))
  def sendWithKey(message: A, key: A) = sendMessage(producer, producerRecordWithKey(topic, message, key))

  def sendStream(stream: Stream[A]) = {
    val iter = stream.iterator
    while (iter.hasNext) {
      send(iter.next())
    }
  }

  def close() = producer.close()
  
  private def producerRecord(topic: String, message: A): ProducerRecord[A, A] = new ProducerRecord[A, A](topic, message)
  private def producerRecordWithKey(topic: String, message: A, key: A): ProducerRecord[A, A] = new ProducerRecord[A, A](topic, key, message)
  private def sendMessage(producer: KafkaProducer[A, A], message: ProducerRecord[A, A]) = producer.send(message)
}

object Producer {
  def apply[T](topic: String, props: Properties) = new Producer[T](topic) {
    override val config = props
  }
}