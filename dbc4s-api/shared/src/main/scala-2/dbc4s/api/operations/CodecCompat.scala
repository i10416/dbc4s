package dbc4s.api.operations
import io.circe.generic.semiauto._
import schema._
trait CodecCompat {
  implicit val emailCodec = deriveCodec[EmailNotifications]
  implicit val scheduleCodec = deriveCodec[Schedule]
}
