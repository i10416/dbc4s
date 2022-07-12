package dbc4s.api.clusters
import dbc4s.utils.DBCSparkRuntimeConfig
import dbc4s.api.clusters.schema._

trait CodecCompat {
  import io.circe.generic.semiauto._
  import org.http4s.circe._
  import io.circe._
  implicit val SparkVersionSchemaEnc: Encoder[DBCSparkRuntimeConfig] =
    new Encoder[DBCSparkRuntimeConfig] {
      def apply(a: DBCSparkRuntimeConfig): Json =
        Json.fromString(a.toString())
    }
  implicit val AutoScaleCodec = deriveCodec[AutoScale]
  implicit val NewClusterCodec = deriveEncoder[NewCluster]
  implicit val JobClusterCodec = deriveEncoder[JobCluster]
}
