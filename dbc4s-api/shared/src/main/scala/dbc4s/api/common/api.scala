package dbc4s.api.common

package object api {
  case class ErrorResponse(error_code: String, message: String)
      extends Throwable
  object codec {
    import io.circe.generic.semiauto._

    implicit val ErrorResCodec = deriveCodec[ErrorResponse]
  }
  object NodeType {
    val i3xlarge = "i3.xlarge"
  }
}
