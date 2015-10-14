package fi.platformv.models
import org.joda.time.DateTime

trait Editable {
  def created: Option[DateTime]
  def changed: Option[DateTime]
}