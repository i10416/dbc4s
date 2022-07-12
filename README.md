[![Release](https://github.com/i10416/dbc4s/actions/workflows/release.yaml/badge.svg)](https://github.com/i10416/dbc4s/actions/workflows/release.yaml)


| scala version | pre-release                                                                                                                                                                                                        | release                                                                                                                               |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------- |
| dbc4s sbt plugin 2.12         | [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/dbc4s-sbt_2.12_1.0.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/dbc4s-sbt_2.12_1.0/) | [![Maven Central](https://img.shields.io/maven-central/v/dev.i10416/dbc4s-sbt_2.12_1.0.svg)](https://search.maven.org/artifact/dev.i10416/dbc4s-sbt_2.12_1.0) |
| dbc4s api 2.12          | [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/dbc4s-api_2.12.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/dbc4s-api_2.12/) | [![Maven Central](https://img.shields.io/maven-central/v/dev.i10416/dbc4s-api_2.12.svg)](https://search.maven.org/artifact/dev.i10416/dbc4s-api_2.12) |
| dbc4s api 2.13         | [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/dbc4s-api_2.13.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/dbc4s-api_2.13/) | [![Maven Central](https://img.shields.io/maven-central/v/dev.i10416/dbc4s-api_2.13.svg)](https://search.maven.org/artifact/dev.i10416/dbc4s-api_2.13) |
| 3.1           | [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/dbc4s-sbt_3.1.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/dbc4s-sbt_3.1/)   | [![Maven Central](https://img.shields.io/maven-central/v/dev.i10416/dbc4s-sbt_3.1.svg)](https://search.maven.org/artifact/dev.i10416/dbc4s-sbt_3.1)   |



example jobs
- pi: calculate pi
- streaming-jobs: streaming job


Libraries
- dbc4s-api: databricks api types
- dbc4s-sbt-plugin: upload uber jar, create, update and delete job from project


## How to Use

```scala
addSbtPlugin("dev.i10416" %% "dbc4s-sbt" % "<version>")
```

```scala
val foo = project
    .in(file("."))
    .enablePlugins(DBC4sPlugin)
    .settings(
      assembly / mainClass := Some("com.example.App"),
      dbc4sApiToken := "",
      dbc4sHost := "your.cloud.databricks.com",
    )
```

```sh
// upload jar job to databricks
sbt dbc4sUpload

// upload and create jar job
sbt dbc4sCreateJob
```
