lazy val commonSettings = Seq(
  organization := "me.benetis",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-Ypartial-unification",
    "-deprecation",
    "-encoding", "UTF-8",
    "-language:higherKinds",
    "-language:postfixOps",
    "-feature",
    "-Xfatal-warnings"
  ),
  libraryDependencies ++= Seq(
    "joda-time" % "joda-time"  % "2.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "ch.qos.logback"  %  "logback-classic"     % "1.2.3",
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "org.typelevel" %% "cats-core" % "1.5.0",
    "org.typelevel" %% "cats-effect" % "1.1.0",
    "mysql" % "mysql-connector-java" % "5.1.38",
    "io.getquill" %% "quill-jdbc" % "2.6.0",
    "io.getquill" %% "quill-core" % "2.6.0",
    "io.getquill" %% "quill-async" % "2.6.0",
    "io.getquill" %% "quill-async-mysql" % "2.6.0"
  )
)

lazy val root = (project in file("."))
  .aggregate(downloader)

lazy val downloader = project
  .settings(commonSettings: _*)
  .settings(
    name := "downloader",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % "0.20.0-M4",
      "org.http4s"      %% "http4s-circe"        % "0.20.0-M4",
      "org.http4s"      %% "http4s-dsl"          % "0.20.0-M4",
      "com.softwaremill.sttp" %% "core" % "1.5.2",
      "org.scala-lang.modules" %% "scala-xml" % "1.1.1"
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4")
  ).dependsOn(shared)

lazy val shared = project
  .settings(commonSettings: _*)
  .settings(
    name := "shared"
  )


//onLoad in Global ~= (_ andThen ("project coordinator" :: _))
