package fi.platformv.utils

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerConfig

import com.typesafe.config.ConfigFactory

import KafkaProducerClientConfig.bootstrapServers
import KafkaProducerClientConfig.keySerializer
import KafkaProducerClientConfig.prefix
import KafkaProducerClientConfig.valueSerializer

trait KafkaProducerClientConfig extends Properties {

  private val prefixWithDot = prefix + "."

  private val allKeys = Seq(bootstrapServers, valueSerializer, keySerializer)

  lazy val typesafeConfig = ConfigFactory.load()

  allKeys.map { key =>
    println(key)
    if (typesafeConfig.hasPath(key))
      put(key.replace(prefixWithDot, ""), typesafeConfig.getString(key))
  }
}

object KafkaProducerClientConfig {
  val prefix = "producer.client"

  // Keys
  val bootstrapServers = s"$prefix.${ProducerConfig.BOOTSTRAP_SERVERS_CONFIG}"
  val valueSerializer = s"$prefix.${ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG}"
  val keySerializer = s"$prefix.${ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG}"

  def apply() = new KafkaProducerClientConfig {}
}

