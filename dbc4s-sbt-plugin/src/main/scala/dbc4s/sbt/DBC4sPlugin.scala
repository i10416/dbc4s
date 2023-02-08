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
import dbc4s.utils.DBCSparkRuntimeConfig
import dbc4s.api.operations.schema.Schedule
import dbc4s.api.common.api.NodeType
import java.time.Instant
object DBC4sPlugin extends AutoPlugin {
  import cats.effect.unsafe.implicits.global

  override def requires: Plugins = sbtassembly.AssemblyPlugin
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    // shared settings
    lazy val dbc4sApiToken = settingKey[String]("Databricks API Token")
    lazy val dbc4sHost = settingKey[String]("Databricks host")

    // job settings
    /** human-readable job name
      */
    lazy val dbc4sJobName = settingKey[String]("Databricks job name")

    /** databricks runtime setting
      */
    lazy val dbc4sJobRuntimeSetting =
      settingKey[DBCSparkRuntimeConfig]("Databricks cluster runtime setting")

    /** cluster node instance setting
      */
    lazy val dbc4sJobClusterNodeType =
      settingKey[String]("Databricks cluster node type. Default is i3xlarge.")

    /** number of workers for this job
      */
    lazy val dbc4sJobWorkers =
      settingKey[Option[Int]]("Number of workers for a job. Default is Some(2)")

    /** where to upload uber jar. Default is /tmp/jobs.
      */
    lazy val dbc4sJobUploadDir =
      settingKey[java.nio.file.Path](
        "Directory to upload jar. Default is /tmp/jobs"
      )
    lazy val dbc4sLibUploadDir = settingKey[java.nio.file.Path](
      "Directory to upload lib jar. Default is /tmp/libs"
    )

    // tasks
    lazy val assemblyArtifact = taskKey[File]("File to be published")
    lazy val dbc4sJobDeploy = taskKey[Unit]("deploy databricks jar job")
    lazy val dbc4sJobLibs = taskKey[Seq[Lib]]("Databricks job dependencies")
    lazy val dbc4sCreateJob = taskKey[Long]("create jar job")
    lazy val dbc4sLibUpload = taskKey[Lib]("upload jvm lib")
    lazy val dbc4sJobUpload =
      taskKey[Lib]("upload uber jar for jar job")
  }
  import autoImport._
  override def projectSettings: Seq[Setting[_]] = Seq(
    // default settings
    dbc4sHost := "",
    dbc4sApiToken := "",
    dbc4sJobName := s"dbc4s-job-${Instant.now().getEpochSecond()}",
    dbc4sJobWorkers := Some(2),
    dbc4sJobRuntimeSetting := DBCSparkRuntimeConfig(
      10,
      4,
      false,
      false,
      false,
      false,
      false,
      "2.12"
    ),
    dbc4sJobClusterNodeType := NodeType.i3xlarge,
    dbc4sJobUploadDir := java.nio.file.Path.of("/tmp/jobs"),
    dbc4sLibUploadDir := java.nio.file.Path.of("/tmp/libs"),
    dbc4sJobLibs := Seq(),

    // tasks
    assemblyArtifact := sbtassembly.AssemblyKeys.assembly.value,
    dbc4sCreateJob := {

      val savedLib = dbc4sJobUpload.value
      val payload = CraeteJobPayload(
        dbc4sJobName.value,
        Seq(
          JobTaskSetting(
            s"${name.value}_task",
            Seq(savedLib),
            SparkJarTask(
              (sbtassembly.AssemblyKeys.assembly / mainClass).value.get
            ),
            Some(
              NewCluster(
                dbc4sJobRuntimeSetting.value,
                dbc4sJobClusterNodeType.value,
                dbc4sJobWorkers.value
              )
            ),
            None
          )
        ),
        Seq.empty
      )
      val conf = DBCConfig.from(dbc4sApiToken.value, dbc4sHost.value)
      val client = conf.fold(
        msg =>
          throw new Exception(msg.foldLeft("") { case (acc, line) =>
            acc + "\n" + line
          }),
        new DatabricksClient(_)
      )

      val id = client.createJob(payload).unsafeRunSync()
      sbt.Keys.streams.value.log.info(s"Successfully create jar job: $id")
      id
    },
    dbc4sLibUpload := {
      val _ = (Compile / packageBin).value
      val jarLoc = (Compile / packageBin / artifactPath).value
      val savedLocation = dbc4sLibUploadDir.value / jarLoc.getName()
      val conf = DBCConfig.from(dbc4sApiToken.value, dbc4sHost.value)
      val client = conf.fold(
        msg =>
          throw new Exception(msg.foldLeft("") { case (acc, line) =>
            acc + "\n" + line
          }),
        new DatabricksClient(_)
      )
      Files[effect.IO]
        .readAll(fs2.io.file.Path.fromNioPath(jarLoc.toPath()))
        .through(
          client.upload(s"dbfs:$savedLocation")
        )
        .compile
        .drain
        .as(effect.ExitCode.Success)
        .unsafeRunSync()
      val jar = schema.Jar(jar = "dbfs:$savedLocation")
      jar
    },
    dbc4sJobUpload := {
      val savedLocation =
        dbc4sJobUploadDir.value / assemblyArtifact.value.getName()

      val conf = DBCConfig.from(dbc4sApiToken.value, dbc4sHost.value)
      val client = conf.fold(
        msg =>
          throw new Exception(msg.foldLeft("") { case (acc, line) =>
            acc + "\n" + line
          }),
        new DatabricksClient(_)
      )
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
    dbc4sJobDeploy := {
      val _ = dbc4sJobUpload.value
    }
  )
}
