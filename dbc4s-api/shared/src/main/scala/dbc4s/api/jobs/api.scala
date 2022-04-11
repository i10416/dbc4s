package dbc4s.api.jobs
import cats.effect.IO
import dbc4s.api.DBCConfig
import dbc4s.api.DBCClient
import dbc4s.api.DatabricksClient
import org.http4s.ember.client.EmberClientBuilder
import cats.effect.kernel.Resource
import dbc4s.api.jobs.schema.{TriggerOnetimePayload, TriggerJobResponse}
import dbc4s.api.jobs.schema.codec._
import dbc4s.api.jobs.schema.CraeteJobPayload
import dbc4s.api.jobs.schema.CreateJobResponse
import org.http4s.Uri
trait API extends DBCClient[IO] {
  this: DatabricksClient =>
  def createJob(payload: CraeteJobPayload): IO[Long] = {
    EmberClientBuilder
      .default[IO]
      .build
      .use { client =>
        post[CraeteJobPayload, CreateJobResponse](
          client,
          payload,
          uri =>
            uri.copy(path = Uri.Path.unsafeFromString("api/2.1/jobs/create")),
          dbcConfig
        )
      }
      .map(_.job_id)
  }
}
