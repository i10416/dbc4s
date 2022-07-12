package dbc4s.api.common

import dbc4s.api.common.api.ErrorResponse
trait CodecCompat {
    import io.circe.generic.semiauto._

    implicit val ErrorResCodec = deriveCodec[ErrorResponse]
}