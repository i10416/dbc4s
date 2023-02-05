package dbc4s.api.jobs

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
    implicit val libDec = new Encoder[Lib] {
      override def apply(a: Lib): Json = a match {
        case Jar(jar) => Json.obj(("jar", Json.fromString(jar)))
      }
    }
    import dbc4s.api.jobs.schema.codec._
    import dbc4s.api.clusters.schema.codec._

    implicit val sparkJarTaskCodec = deriveCodec[SparkJarTask]
    implicit val JobTaskSettingCodec = deriveEncoder[JobTaskSetting]
    implicit val cjr = deriveCodec[CreateJobResponse]
    implicit val tjr = deriveCodec[TriggerJobResponse]
    implicit val tjp = deriveEncoder[CraeteJobPayload]
    implicit val tjpEntity = jsonEncoderOf[IO, CraeteJobPayload]
    implicit val tjrEntity = jsonOf[IO, CreateJobResponse]
  }
}
