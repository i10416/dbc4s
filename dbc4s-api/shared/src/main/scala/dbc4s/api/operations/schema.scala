package dbc4s.api.operations
import io.circe.generic.semiauto._
package object schema {

  case class EmailNotifications(
      on_start: Seq[String] = Seq.empty,
      on_success: Seq[String] = Seq.empty,
      on_failure: Seq[String] = Seq.empty,
      no_alert_for_skipped_runs: Boolean = false
  )
  case class Schedule(
      quartz_cron_expression: String,
      timezone_id: String,
      pause_status: String
  )
  object PauseStatus {
    val PAUSED = "PAUSED"
    val UNPAUSED = "UNPAUSED"
  }
  object codec extends CodecCompat
}
