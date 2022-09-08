resolvers ++= Resolver.sonatypeOssRepos("snapshots")
resolvers ++= Resolver.sonatypeOssRepos("releases")
val dbc4sVersion = "0.0.0+28-f2c46f86-SNAPSHOT"
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.1")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.2.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.10.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("dev.i10416" %% "dbc4s-sbt" % dbc4sVersion)
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
libraryDependencies += "dev.i10416" %% "dbc4s-api" % dbc4sVersion
