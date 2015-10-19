package fi.platformv.utils

import java.util.Properties

import com.typesafe.config.ConfigFactory

import KafkaConsumerConfig._

trait KafkaConsumerConfig extends Properties {

  private val prefixWithDot = prefix + "."

  private val allKeys = Seq(zkConnect, groupId)

  lazy val typesafeConfig = ConfigFactory.load()

  allKeys.map { key =>
    if (typesafeConfig.hasPath(key))
      put(key.replace(prefixWithDot, ""), typesafeConfig.getString(key))
  }
}

object KafkaConsumerConfig {
  val prefix = "consumer"
  
  // Keys
  val zkConnect = s"$prefix.zookeeper.connect"
  val groupId = s"$prefix.group.id"

  def apply() = new KafkaConsumerConfig {}
}

