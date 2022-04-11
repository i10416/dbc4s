package dbc4s.utils
import scala.util.parsing.combinator._

/** @param dbcRuntimeMajorRelease
  *   Databricks Runtime major release
  * @param dbcRuntimeFeatureRelease
  *   Databricks Runtime feature release
  * @param cpu
  *   version (with -ml only)
  *
  * @param extendedSupport
  *   Extended Support
  *
  * @param gpu
  *   GPU-enabled
  *
  * @param ml
  *   Machine learning
  *
  * @param Photon
  * @see
  *   [[https://docs.databricks.com/dev-tools/api/index.html#runtime-version-strings]]
  */
case class DBCSparkVersionScheme(
    dbcRuntimeMajorRelease: Int,
    dbcRuntimeFeatureRelease: Int,
    cpu: Boolean,
    extendedSupport: Boolean,
    gpu: Boolean,
    ml: Boolean,
    photon: Boolean,
    scalaVersion: String
) {

  val flags = Seq(
    (cpu, "-cpu"),
    (extendedSupport, "-esr"),
    (gpu, "-gpu"),
    (ml, "-ml"),
    (photon, "-photon")
  ).map {
    case (true, flag) => flag
    case _            => ""
  }.mkString
  override def toString(): String =
    s"$dbcRuntimeMajorRelease.$dbcRuntimeFeatureRelease.x$flags-scala$scalaVersion"
}

object DBCSparkVersionScheme extends RegexParsers {

  def number: Parser[Int] = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def flag(p: Parser[Any]) = opt(p).^^(maybe => !maybe.isEmpty)
  def runtimeVersion = (number ~ "." ~ number ~ ".x").map {
    case major ~ "." ~ feature ~ ".x" => (major, feature)
    case _                            => ???
  }
  def scalaVersion = { "-scala" ~ number ~ "." ~ number }.map {
    case "-scala" ~ major ~ "." ~ minor if major == 2 => s"$major.$minor"
    case _                                            => ???
  }

  def versionSchemeParser: Parser[DBCSparkVersionScheme] = {
    val spec =
      runtimeVersion ~ flag("-cpu".r) ~ flag("-esr") ~ flag("-gpu") ~ flag(
        "-ml"
      ) ~ flag("-photon") ~ scalaVersion
    spec
      .map { case (rtMajor, rtMinor) ~ cpu ~ esr ~ gpu ~ ml ~ photon ~ scalaV =>
        DBCSparkVersionScheme(
          rtMajor,
          rtMinor,
          cpu,
          esr,
          gpu,
          ml,
          photon,
          scalaV
        )
      }
  }

  def apply(rawValue: String): Either[String, DBCSparkVersionScheme] =
    parseAll(versionSchemeParser, rawValue) match {
      case Error(msg, next)   => Left(msg)
      case Failure(msg, next) => Left(msg)
      case Success(result, _) => Right(result)
    }
}
