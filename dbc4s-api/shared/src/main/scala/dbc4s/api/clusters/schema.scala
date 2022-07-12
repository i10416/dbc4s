package dbc4s.api.clusters

import dbc4s.utils.DBCSparkVersionScheme

package object schema {

  case class JobCluster(
      job_cluster_key: String,
      new_cluster: Option[NewCluster] = None
  )

  /** https://docs.databricks.com/dev-tools/api/latest/clusters.html
    *
    * @param node_type_id
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
      auto_scale: Option[AutoScale] = None,
      spark_conf: Map[String, String] = Map.empty
  )

  object NewCluster {
    def singleNode(
        sparkVersion: DBCSparkVersionScheme,
        nodeTypeId: String,
        autoScale: Option[AutoScale] = None,
        sparkConf: Map[String, String] = Map.empty
    ): NewCluster = {
      NewCluster(
        spark_version = sparkVersion,
        node_type_id = nodeTypeId,
        None,
        Some(nodeTypeId),
        autoScale,
        Map(
          "spark.databricks.cluster.profile" -> "singleNode",
          "spark.master" -> "local[*]"
        ) ++ sparkConf
      )
    }
  }

  /** @param min_workers
    *   The minimum number of workers to which the cluster can scale down when
    *   underutilized. It is also the initial number of workers the cluster has
    *   after creation.
    * @param max_workers
    *   The maximum number of workers to which the cluster can scale up when
    *   overloaded. max_workers must be strictly greater than min_workers.
    */
  case class AutoScale(min_workers: Int, max_workers: Int)

  object codec extends CodecCompat
}