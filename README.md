example jobs
- pi: calculate pi
- streaming-jobs: streaming job


Libraries
- dbc4s-api: databricks api types
- dbc4s-sbt-plugin: upload uber jar, create, update and delete job from project


## How to Use

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
