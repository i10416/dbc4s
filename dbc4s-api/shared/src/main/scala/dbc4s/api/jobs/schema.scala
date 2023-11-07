package dbc4s.api.jobs

import org.http4s.EntityEncoder
import org.http4s.EntityDecoder

package object schema {

  import dbc4s.api.operations.schema._
  import dbc4s.api.clusters.schema._
  case class CraeteJobPayload(
      name: String,
      tasks: Seq[JobTaskSetting],
      job_clusters: Seq[JobCluster],
      schedule: Option[Schedule] = None,
      email_notifications: EmailNotifications = EmailNotifications(),
      timeout_seconds: Option[Int] = None,
      max_concurrent_runs: Option[Int] = None,
      format: String = TaskFormat.MULTI_TASK
  )
  case class CreateJobResponse(
      job_id: Long
  )

  case class TriggerOnetimePayload(
      tasks: Seq[JobTaskSetting],
      run_name: String = "Untitled",
      new_cluster: Option[NewCluster],
      timeout_seconds: Option[Int] = None,
      idempotency_token: Option[String] = None
  )

  case class TriggerJobResponse(
      run_id: Long
  )

  case class JobTaskSetting(
      task_key: String,
      libraries: Seq[Lib],
      spark_jar_task: SparkJarTask,
      new_cluster: Option[NewCluster],
      existing_cluster_id: Option[String] = None,
      timeout_seconds: Option[Int] = None,
      description: String = "",
      max_retries: Option[Int] = None,
      min_retry_interval_millis: Option[Int] = None,
      retry_on_timeout: Boolean = false
  )

  case class SparkJarTask(
      main_class_name: String,
      parameters: Seq[String] = Seq.empty
  )

  object TaskFormat {
    val SINGLE_TASK = "SINGLE_TASK"
    val MULTI_TASK = "MULTI_TASK"
  }

  sealed trait Lib
  case class Jar(jar: String) extends Lib
  object codec {
    import io.circe.generic.semiauto._
    import org.http4s.circe._
    import cats.effect.IO
    import io.circe._
    import dbc4s.api.operations.schema.codec._
    implicit val libDec: Encoder[Lib] = new Encoder[Lib] {
      override def apply(a: Lib): Json = a match {
        case Jar(jar) => Json.obj(("jar", Json.fromString(jar)))
      }
    }
    import dbc4s.api.jobs.schema.codec._
    import dbc4s.api.clusters.schema.codec._

    implicit val sparkJarTaskCodec: Codec.AsObject[SparkJarTask] =
      deriveCodec[SparkJarTask]
    implicit val JobTaskSettingCodec: Encoder.AsObject[JobTaskSetting] =
      deriveEncoder[JobTaskSetting]
    implicit val cjr: Codec.AsObject[CreateJobResponse] =
      deriveCodec[CreateJobResponse]
    implicit val tjr: Codec.AsObject[TriggerJobResponse] =
      deriveCodec[TriggerJobResponse]
    implicit val tjp: Encoder.AsObject[CraeteJobPayload] =
      deriveEncoder[CraeteJobPayload]
    implicit val tjpEntity: EntityEncoder[IO, CraeteJobPayload] =
      jsonEncoderOf[IO, CraeteJobPayload]
    implicit val tjrEntity: EntityDecoder[IO, CreateJobResponse] =
      jsonOf[IO, CreateJobResponse]
  }
}
