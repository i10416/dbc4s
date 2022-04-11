package dbc4s.sbt
import sbt._
import sbt.Keys._
import dbc4s.api._
import fs2.io.file._
import sbtassembly._
import cats.effect
import org.http4s.Uri
import dbc4s.api.jobs.schema._
import dbc4s.api.jobs.schema.Lib
import dbc4s.api.jobs.schema.CraeteJobPayload
import dbc4s.api.jobs.schema.JobTaskSetting
import dbc4s.api.jobs.schema.SparkJarTask
import dbc4s.api.jobs.schema
import dbc4s.api.clusters.schema.JobCluster
import dbc4s.api.clusters.schema.NewCluster
import dbc4s.utils.DBCSparkVersionScheme
import dbc4s.api.operations.schema.Schedule
import dbc4s.api.common.api.NodeType
object DBC4sPlugin extends AutoPlugin {
  import cats.effect.unsafe.implicits.global

  override def requires: Plugins = sbtassembly.AssemblyPlugin
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val dbc4sApiToken = settingKey[String]("Databricks API Token")
    lazy val dbc4sHost = settingKey[String]("Databricks host")
    lazy val dbc4sUploadDir =
      settingKey[java.nio.file.Path]("Directory to upload jar")
    lazy val assemblyArtifact = taskKey[File]("File to be published")
    lazy val deploy = taskKey[Unit]("deploy databricks jar job")
    lazy val dbc4sJobLibs = taskKey[Seq[Lib]]("Databricks job deps")
    lazy val dbc4sCreateJob = taskKey[Long]("create jar job")
    lazy val dbc4sUpload =
      taskKey[Lib]("upload uber jar for jar job")
  }
  import autoImport._
  override def projectSettings: Seq[Setting[_]] = Seq(
    dbc4sHost := "",
    dbc4sApiToken := "",
    dbc4sUploadDir := java.nio.file.Path.of("/tmp/jobs"),
    dbc4sJobLibs := Seq(),
    assemblyArtifact := sbtassembly.AssemblyKeys.assembly.value,
    dbc4sCreateJob := {

      val savedLib = dbc4sUpload.value
      val payload = CraeteJobPayload(
        s"${name.value.capitalize} - ${description.value}",
        Seq(
          JobTaskSetting(
            s"${name.value}_task",
            Seq(savedLib),
            SparkJarTask(
              (sbtassembly.AssemblyKeys.assembly / mainClass).value.get
            ),
            Some(
              NewCluster(
                DBCSparkVersionScheme(
                  10,
                  4,
                  false,
                  false,
                  false,
                  false,
                  false,
                  "2.12"
                ),
                NodeType.i3xlarge,
                Some(2)
              )
            ),
            None
          )
        ),
        Seq.empty
      )
      val client =
        new DatabricksClient(DBCConfig(dbc4sApiToken.value, dbc4sHost.value))
      val id = client.createJob(payload).unsafeRunSync()
      sbt.Keys.streams.value.log.info(s"Successfully create jar job: $id")
      id
    },
    dbc4sUpload := {
      val savedLocation =
        dbc4sUploadDir.value / assemblyArtifact.value.getName()

      val client =
        new DatabricksClient(DBCConfig(dbc4sApiToken.value, dbc4sHost.value))
      Files[effect.IO]
        .readAll(fs2.io.file.Path.fromNioPath(assemblyArtifact.value.toPath()))
        .through(
          client.upload(savedLocation.toString())
        )
        .compile
        .drain
        .as(effect.ExitCode.Success)
        .unsafeRunSync()
      val jar = schema.Jar(jar = s"dbfs:$savedLocation")
      sbt.Keys.streams.value.log.info(s"Successfully upload jar at ${jar.jar}")
      jar
    },
    deploy := {
      val _ = dbc4sUpload.value
    }
  )
}