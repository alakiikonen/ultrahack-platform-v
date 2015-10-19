package models

import kafka.consumer.{ Consumer => KafkaConsumer, ConsumerIterator, Whitelist, ConsumerConfig }
import kafka.serializer.{ DefaultDecoder, Decoder }
import scala.collection.JavaConversions._
import fi.platformv.models.ConsumerBase

case class StreamConsumer(topics: List[String]) extends ConsumerBase {
  //topics to listen
  //private val filterSpec = new Whitelist(topics.mkString(","))
  private val filterSpec = new Whitelist(topics.mkString(","))

  protected val keyDecoder: Decoder[Array[Byte]] = new DefaultDecoder()
  protected val valueDecoder: Decoder[Array[Byte]] = new DefaultDecoder()

  private lazy val consumer = KafkaConsumer.create(new ConsumerConfig(config))
  private lazy val stream = consumer.createMessageStreamsByFilter(filterSpec, 1, keyDecoder, valueDecoder).get(0)
  
  def read(): Stream[String] = Stream.cons(new String(stream.head.message()), read())
  
  def close(): Unit = consumer.shutdown()
}

object StreamConsumer {
  def apply(topics: List[String], kDecoder: Decoder[Array[Byte]], vDecoder: Decoder[Array[Byte]]) = new StreamConsumer(topics) {
    override val keyDecoder = kDecoder
    override val valueDecoder = vDecoder
  }
}

case class SingleTopicConsumer(topic: String) extends ConsumerBase {
  private lazy val consumer = KafkaConsumer.create(new ConsumerConfig(config))
  val threadNum = 1

  private lazy val consumerMap = consumer.createMessageStreams(Map(topic -> threadNum))
  private lazy val stream = consumerMap.getOrElse(topic, List()).head

  def close(): Unit = consumer.shutdown()
  
  def read(): Stream[String] = Stream.cons(new String(stream.head.message()), read())
}