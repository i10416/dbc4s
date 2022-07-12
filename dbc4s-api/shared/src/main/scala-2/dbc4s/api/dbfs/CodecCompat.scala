package dbc4s.api.dbfs
import io.circe.generic.semiauto._
import org.http4s.circe._
import cats.effect.IO
import dbc4s.api.dbfs.schema._
trait CodecCompat {
  implicit val StartUploadPayloadEntityCodec = deriveEncoder[StartUploadPayload]
  implicit val StartUploadPayloadEncoder = jsonEncoderOf[IO, StartUploadPayload]
  implicit val StartUploadResponseEntityCodec =
    deriveDecoder[StartUploadResponse]
  implicit val StartUploadResponseDecoder = jsonOf[IO, StartUploadResponse]

  implicit val AppendChunkRequestEnc = deriveEncoder[AppendChunkRequest]
  implicit val AppendChunkRequestEntityEnc =
    jsonEncoderOf[IO, AppendChunkRequest]
  implicit val FinishUploadPayloadEnc = deriveEncoder[FinishUploadPayload]
  implicit val FinishUploadPayloadEntityEnc =
    jsonEncoderOf[IO, FinishUploadPayload]
}
