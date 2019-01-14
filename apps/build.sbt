lazy val commonSettings = Seq(
  organization := "me.benetis",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq("-Ypartial-unification"),
  libraryDependencies ++= Seq(
    "joda-time" % "joda-time"  % "2.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "ch.qos.logback"  %  "logback-classic"     % "1.2.3",
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "org.tpolecat" %% "doobie-core"      % "0.6.0",
    "org.tpolecat" %% "doobie-hikari"    % "0.6.0",
    "org.typelevel" %% "cats-core" % "1.5.0",
    "org.typelevel" %% "cats-effect" % "1.1.0",
    "com.zaxxer" % "HikariCP" % "3.3.0"
  )
)

lazy val root = (project in file("."))
  .aggregate(downloader)

lazy val downloader = project
  .settings(commonSettings: _*)
  .settings(
    name := "downloader",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % "0.18.21",
      "org.http4s"      %% "http4s-circe"        % "0.18.21",
      "org.http4s"      %% "http4s-dsl"          % "0.18.21",
      "com.softwaremill.sttp" %% "core" % "1.5.2",
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
