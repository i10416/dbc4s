val scala212 = "2.12.16"
val scala213 = "2.13.8"
Global / onChangedBuildSource := ReloadOnSourceChanges
scalaVersion := scala212
// val uploadArtifact = TaskKey(...)

inThisBuild(
  Seq(
    organization := "dev.i10416",
    organizationHomepage := Some(new URL("https://github.com/i10416")),
    homepage := Some(url("https://github.com/i10416/dbc4s")),
    startYear := Some(2022),
    licenses := Seq(
      "Apache 2.0" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    versionScheme := Some("early-semver"),
    developers := List(
      Developer(
        "i10416",
        "110416",
        "contact.110416+dbc4s@gmail.com",
        url("https://github.com/i10416")
      )
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/i10416/dbc4s"),
        "scm:git@github.com;i10416/dbc4s.git"
      )
    )
  )
)

val commonSettings = Seq(
  scalacOptions ++= {
    import sbt.Opts.compile._
    Seq(
      deprecation,
      unchecked,
      "-feature"
    ) ++ {
      if (scalaVersion.value.startsWith("2.12")) Seq("-language:higherKinds")
      else Seq.empty
    }
  }
)

lazy val noPublishSettings = Seq(
  publish := (()),
  publishLocal := (()),
  publishTo := None
)

lazy val pi = project
  .in(file("pi"))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .enablePlugins(DBC4sPlugin)
  .settings(
    run / fork := true,
    run / javaOptions := Seq(
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
    ),
    assembly / mainClass := Some("dev.i10416.playground.Program"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    },
    dbc4sApiToken := "",
    dbc4sHost := "",
    dbc4sJobName := "Example -- calculate Pi",
    libraryDependencies ++= Deps.sparkAll
      .map(_ % "provided") ++ Deps.collectionCompat ++ Deps.catsEffect.value
  )

lazy val dbcapi = crossProject(JVMPlatform, JSPlatform)
  .in(file("dbc4s-api"))
  .settings(commonSettings)
  .settings(
    name := "dbc4s-api",
    crossScalaVersions := Seq(scala212, scala213),
    run / javaOptions := Seq(
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/sun.security.action=ALL-UNNAMED"
    ),
    libraryDependencies ++= Deps.catsEffect.value ++ Deps.fs2.value ++ Deps.http4s.value// ++ Deps.parserCombinator.value
  )
lazy val dbc4sPlugin = project
  .in(file("dbc4s-sbt-plugin"))
  .enablePlugins(SbtPlugin)
  .settings(commonSettings)
  .settings(
    name := "dbc4s-sbt",
    addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.2.0"),
    sbtPlugin := true
  )
  .dependsOn(dbcapi.jvm)

lazy val root = project
  .in(file("."))
  .aggregate(pi, dbcapi.jvm, dbc4sPlugin)
  .settings(noPublishSettings)
  .settings(
    name := "spark-jobs"
  )
