package dbc4s.api.dbfs
import dbc4s.api.DBCConfig
import dbc4s.api.dbfs.schema._
import cats.effect.IO
import fs2.io.file.Path
import io.circe.syntax._
import org.http4s.Request
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.Method
import org.http4s.Uri
import org.http4s.Credentials
import org.http4s.AuthScheme
import org.http4s.MediaType
import org.http4s.client.Client
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import fs2.Collector
import fs2.Chunk
import fs2.io.file.Files
import dbc4s.api.DBCClient
import dbc4s.api.DatabricksClient
trait API extends DBCClient[IO] {

  this: DatabricksClient =>
  def list(): fs2.Stream[IO, String] = ???
  def delete(): IO[Unit] = ???

  /** @see
    *   [[https://docs.databricks.com/dev-tools/api/latest/examples.html#upload-a-big-file-into-dbfs]]
    */
  def upload(
      dbfsPath: String
  ): fs2.Pipe[IO, Byte, Int] = {
    val req = StartUploadPayload(dbfsPath)
    data =>
      for {
        client <- fs2.Stream
          .resource(EmberClientBuilder.default[IO].build)
        res <- fs2.Stream.eval(
          post[StartUploadPayload, StartUploadResponse](
            client,
            req,
            _ / "dbfs" / "create",
            dbcConfig
          )
        )
        progress <- data
          .chunkN(1024 * 1024) // at most 1MB per request
          .flatMap(uploadChunk(client, res.handle))
          .onFinalize[IO](
            post[FinishUploadPayload, Unit](
              client,
              FinishUploadPayload(res.handle),
              _ / "dbfs" / "close",
              dbcConfig
            )
          )
      } yield progress
  }

  private def uploadChunk(
      client: Client[IO],
      handle: Long
  )(chunk: Chunk[Byte]): fs2.Stream[IO, Int] = {
    fs2.Stream
      .chunk(chunk)
      .through(fs2.text.base64.encode)
      .evalMap(data =>
        post[AppendChunkRequest, Unit](
          client,
          AppendChunkRequest(handle, data),
          _ / "dbfs" / "add-block",
          dbcConfig
        )
      )
      .as(chunk.size)
  }

}
