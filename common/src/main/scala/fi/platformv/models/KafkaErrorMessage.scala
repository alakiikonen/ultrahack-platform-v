package fi.platformv.models

import org.joda.time.DateTime

import play.api.libs.json.JsObject
import play.api.libs.json.Json

object KafkaErrorType extends Enumeration {
  import play.api.libs.json._

  type KafkaErrorType = Value

  val TIMEOUT = Value("timeout")
  val INVALID_RESPONSE_STATUS = Value("invalidResponseStatus")
  val SERVICE_UNAVAILABLE = Value("serviceUnavailable")
  val DATABASE_WRITE_ERROR = Value("databaseWriteError")
  val OTHER = Value("Other")

  implicit object kafkaErrorTypeFormat extends Format[KafkaErrorType] {
    def reads(json: JsValue): JsResult[KafkaErrorType] = JsSuccess(KafkaErrorType.withName(json.as[String]))
    def writes(group: KafkaErrorType): JsString = JsString(group.toString)
  }
}

import KafkaErrorType._

case class KafkaErrorMessage(
  errorType: KafkaErrorType,
  errorMsg: String,
  sourceType: String,
  sourceId: String,
  timestamp: DateTime,
  extra: Option[JsObject]
)

object KafkaErrorMessage {
  implicit val kafkaErrorMessageFormat = Json.format[KafkaErrorMessage]
}