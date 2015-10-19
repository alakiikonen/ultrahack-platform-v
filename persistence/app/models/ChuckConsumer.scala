package models
/*
import kafka.api.FetchRequestBuilder
import kafka.consumer.SimpleConsumer
import fi.platformv.KafkaConfig

case class ChunkConsumer(topics: List[String], partition: Int = 1, offset: Long = 1L, fetchSize: Int = 100) extends Consumer(topics) {

  private val clientId = kafkaConfig.getProperty("clientId")

  val simpleConsumer = new SimpleConsumer(
    kafkaConfig.getProperty("host"),
    kafkaConfig.getProperty("port").toInt,
    kafkaConfig.getProperty("timeOut").toInt,
    kafkaConfig.getProperty("bufferSize").toInt,
    clientId)
  
  override def shutdown(): Unit = simpleConsumer.close()

  def read(): Iterable[String] = {
    
    val fetchRequest = new FetchRequestBuilder().clientId(clientId)
    
    for(topic <- topics) {
      fetchRequest.addFetch(topic, partition, offset, fetchSize)
    }
    
    val fetchResponse = simpleConsumer.fetch(fetchRequest.build())
    
    fetchResponse.data.values.flatMap { topic =>
      topic.messages.toList.map { mao =>
        val payload =  mao.message.payload

        //ugliest part of the code. Thanks to kafka
        val data = Array.fill[Byte](payload.limit)(0)
        payload.get(data)
        new String(data)
      }
    }
  }
}
*/