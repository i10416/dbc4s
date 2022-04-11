package dbc4s.api.dbfs
import io.circe.generic.semiauto._
import org.http4s.circe._
import cats.effect.IO
package object schema {

  case class StartUploadPayload(path: String, overwrite: Boolean = true)
  implicit val StartUploadPayloadEntityCodec = deriveEncoder[StartUploadPayload]
  implicit val StartUploadPayloadEncoder = jsonEncoderOf[IO, StartUploadPayload]
  case class StartUploadResponse(handle: Long)
  implicit val StartUploadResponseEntityCodec =
    deriveDecoder[StartUploadResponse]
  implicit val StartUploadResponseDecoder = jsonOf[IO, StartUploadResponse]

  implicit val AppendChunkRequestEnc = deriveEncoder[AppendChunkRequest]
  implicit val AppendChunkRequestEntityEnc =
    jsonEncoderOf[IO, AppendChunkRequest]
  case class AppendChunkRequest(handle: Long, data: String)
  implicit val FinishUploadPayloadEnc = deriveEncoder[FinishUploadPayload]
  implicit val FinishUploadPayloadEntityEnc =
    jsonEncoderOf[IO, FinishUploadPayload]
  case class FinishUploadPayload(handle: Long)

}
