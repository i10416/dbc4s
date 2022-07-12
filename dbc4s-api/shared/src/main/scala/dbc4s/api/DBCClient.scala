package dbc4s.api
import cats.effect.IO
import org.http4s.client._
import org.http4s.{EntityEncoder, EntityDecoder}
import org.http4s.Uri
import org.http4s.{Request, Method, Credentials, AuthScheme, MediaType}
import cats.effect.kernel.Async
import scala.language.higherKinds
import cats.effect.std.Console
import dbc4s.api.common.api.ErrorResponse
abstract class DBCClient[F[_]: Async] {

  type FEntityEncoder[T] = EntityEncoder[F, T]
  type FEntityDecoder[T] = EntityDecoder[F, T]

  protected def post[Req: FEntityEncoder, Res: FEntityDecoder](
      client: Client[F],
      req: Req,
      endpointBuilder: Uri => Uri,
      dbcConfig: DBCConfig
  ): F[Res] = {
    val base = Uri.unsafeFromString(s"https://${dbcConfig.host}/api/2.0")

    val endpoint = endpointBuilder(base)
    import org.http4s.headers._
    import org.http4s.client.middleware.FollowRedirect
    FollowRedirect
      .apply(Int.MaxValue)(client)
      .expectOr(
        Request[F](Method.POST, endpoint)
          .withHeaders(
            Authorization(
              Credentials.Token(AuthScheme.Bearer, dbcConfig.apiToken)
            )
          )
          .withContentType(
            `Content-Type`(MediaType.application.json)
          )
          .withEntity(req)
      )(err => {
        import cats.syntax.all._
        import cats.effect.syntax.all._
        import dbc4s.api.common.api.codec._
        implicit val ErrorResEntity = org.http4s.circe.jsonOf[F, ErrorResponse]
        for {
          r <- err.as[ErrorResponse]
          _ <- Console.make[F].error(r)
        } yield new Exception("err")
      })
  }
}
