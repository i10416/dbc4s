package dbc4s.api.clusters

import dbc4s.utils.DBCSparkVersionScheme

package object schema {

  case class JobCluster(
      job_cluster_key: String,
      new_cluster: Option[NewCluster] = None
  )

  /** @param node_type_id
    *   This field encodes, through a single value, the resources available to
    *   each of the Spark nodes in this cluster. For example, the Spark nodes
    *   can be provisioned and optimized for memory or compute intensive
    *   workloads A list of available node types can be retrieved by using the
    *   List node types API call. This field is required.
    *
    * @param deriver_node_type_id
    *   The node type of the Spark driver. This field is optional; if unset, the
    *   driver node type is set as the same value as node_type_id defined above.
    */
  case class NewCluster(
      spark_version: DBCSparkVersionScheme,
      node_type_id: String,
      num_workers: Option[Int] = None,
      driver_node_type_id: Option[String] = None,
      auto_scale: Option[AutoScale] = None
  )

  /** @param min_workers
    *   The minimum number of workers to which the cluster can scale down when
    *   underutilized. It is also the initial number of workers the cluster has
    *   after creation.
    * @param max_workers
    *   The maximum number of workers to which the cluster can scale up when
    *   overloaded. max_workers must be strictly greater than min_workers.
    */
  case class AutoScale(min_workers: Int, max_workers: Int)

  object codec {
    import io.circe.generic.semiauto._
    import org.http4s.circe._
    import io.circe._
    implicit val SparkVersionSchemaEnc: Encoder[DBCSparkVersionScheme] =
      new Encoder[DBCSparkVersionScheme] {
        def apply(a: DBCSparkVersionScheme): Json =
          Json.fromString(a.toString())
      }
    implicit val AutoScaleCodec = deriveCodec[AutoScale]
    implicit val NewClusterCodec = deriveEncoder[NewCluster]
    implicit val JobClusterCodec = deriveEncoder[JobCluster]

  }
}
