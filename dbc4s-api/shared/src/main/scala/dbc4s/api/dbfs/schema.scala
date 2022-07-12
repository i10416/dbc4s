package dbc4s.api.dbfs

package object schema {
  object codec extends CodecCompat

  case class StartUploadPayload(path: String, overwrite: Boolean = true)
  case class StartUploadResponse(handle: Long)

  case class AppendChunkRequest(handle: Long, data: String)
  case class FinishUploadPayload(handle: Long)

}
