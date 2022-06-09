import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
object Deps {
  val sparkVersion = "3.2.1"
  val scalafixVersion = "0.10.1"
  val sparkBasic = Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion
  )
  val sparkStream = "org.apache.spark" %% "spark-streaming" % sparkVersion

  val sparkAll = sparkBasic :+ sparkStream

  val collectionCompat = Seq(
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0"
  )
  val catsEffect = Def.setting(
    Seq(
      "org.typelevel" %%% "cats-effect" % "3.3.9"
    )
  )
  val parserCombinator = Def.setting(
    Seq(
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.1.1"
    )
  )
  val fs2 = Def.setting(
    Seq(
      "co.fs2" %%% "fs2-core" % "3.2.7",
      "co.fs2" %%% "fs2-io" % "3.2.7"
    )
  )
  val http4sVersion = "0.23.9"

  val http4s = Def.setting(
    Seq(
      "org.http4s" %%% "http4s-core" % http4sVersion,
      "org.http4s" %%% "http4s-dsl" % http4sVersion,
      "org.http4s" %%% "http4s-ember-client" % http4sVersion,
      "org.http4s" %%% "http4s-circe" % http4sVersion,
      "io.circe" %%% "circe-generic" % "0.15.0-M1"
    )
  )

  val scalafix = Seq(
    "ch.epfl.scala" %% "scalafix-core" % scalafixVersion
  )
}
