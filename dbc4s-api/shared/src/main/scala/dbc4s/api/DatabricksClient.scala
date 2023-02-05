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

case class DBCConfig(apiToken: String, host: Host)

object DBCConfig {
  import cats.syntax._
  import cats.syntax.apply._
  import cats.implicits._
  import cats.data.ValidatedNec

  def from(apiToken: String, host: String): ValidatedNec[String, DBCConfig] = {
    type ValidationResult[T] = ValidatedNec[String, T]

    val apiTokenValidated: ValidationResult[String] =
      if (apiToken.nonEmpty) apiToken.validNec
      else "api token must not be empty".invalidNec
    val hostValidated: ValidationResult[Host] =
      Host.fromString(host).toValidNec(s"host is invalid: $host")

    apiTokenValidated product hostValidated map { case (token, host) =>
      DBCConfig(token, host)
    }
  }
}
