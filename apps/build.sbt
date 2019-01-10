lazy val commonSettings = Seq(
  organization := "me.benetis",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  libraryDependencies ++= Seq(
    "joda-time"              % "joda-time"              % "2.7"
  )
)

val Http4sVersion = "0.18.21"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file(".")).aggregate(downloader)

lazy val downloader = project
  .settings(commonSettings: _*)
  .settings(
    name := "downloader",
    scalacOptions ++= Seq("-Ypartial-unification"),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"     %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
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
