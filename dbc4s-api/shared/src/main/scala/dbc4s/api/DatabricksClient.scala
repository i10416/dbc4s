package dbc4s.api
import org.http4s.Uri
import cats.effect.IO
import cats.effect.ExitCode
import fs2.io.file.Path
import fs2.io.file.Files
class DatabricksClient(cfg: DBCConfig) extends dbfs.API with jobs.API {
  def dbcConfig: DBCConfig = cfg
}
object DatabricksClient {
  // def load():IO[DatabricksClient] = ???
}

case class DBCConfig(apiToken: String, host: String)
