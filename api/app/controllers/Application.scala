package controllers

import scala.concurrent.Future.{ successful => resolve }
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.util.Random
import TempSensorData._
import org.joda.time.DateTime

case class TempSensorData(
  id: String,
  time: DateTime,
  temp: Double,
  voltage: Double
)
object TempSensorData {
  implicit val tempSensorDataFormat = Json.format[TempSensorData]
}

class Application extends Controller {
  val rnd = new Random

  def sensor(id: String) = Action {
    Ok(Json.toJson(TempSensorData(id, DateTime.now, rnd.nextDouble()*100, rnd.nextDouble()*5)))
  }
}
