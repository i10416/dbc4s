package dbc4s.api
import org.http4s.Uri
import cats.effect.IO
import cats.effect.ExitCode
import fs2.io.file.Path
import fs2.io.file.Files
import com.comcast.ip4s.Host

import cats.data.Validated
class DatabricksClient(cfg: DBCConfig) extends dbfs.API with jobs.API {
  def dbcConfig: DBCConfig = cfg
}
object DatabricksClient {
  // def load():IO[DatabricksClient] = ???
}

case class DBCConfig(apiToken: String, host:Host)

object DBCConfig {

  def from(apiToken:String,host:String):Either[String,DBCConfig] = {
    Host.fromString(host) match {
      case None =>  Left("token must not be empty")
      case Some(host) => Right(DBCConfig(apiToken,host))
    }
  }
}
