import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

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
    "-Xfatal-warnings",
    "-language:implicitConversions"
  ),
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %%  "monocle-core"  % "1.5.0"),
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val coordinator = project
  .settings(commonSettings: _*)
  .settings(
    test in assembly := {},
    name := "coordinator",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"   % "10.1.8",
"com.typesafe.akka" %% "akka-stream" % "2.5.19",
      "com.softwaremill.sttp" %% "core" % "1.5.2",
      "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
      "joda-time" % "joda-time"  % "2.7",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "org.typelevel" %% "cats-core" % "1.5.0",
      "org.typelevel" %% "cats-effect" % "1.1.0",
      "mysql" % "mysql-connector-java" % "5.1.38",
      "io.getquill" %% "quill-jdbc" % "2.6.0",
      "io.getquill" %% "quill-core" % "2.6.0",
      "io.getquill" %% "quill-async" % "2.6.0",
      "io.getquill" %% "quill-async-mysql" % "2.6.0",
      "com.github.haifengl" %% "smile-scala" % "1.5.2",
      "org.scalaz" %% "scalaz-zio" % "0.9",
      "org.json4s" %% "json4s-native" % "3.6.5",
      "io.suzaku" %% "boopickle" % "1.3.0",
      "com.lihaoyi" %% "autowire" % "0.2.6",
    ),
    assemblyExcludedJars in assembly := {
      val cp = (fullClasspath in assembly).value
      cp filter {_.data.getName == "xmlpull-1.1.3.1.jar"}
    },
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4")
  )
  .dependsOn(sharedJVM)


val webpackResourcesBlob = Def.setting {
  (baseDirectory in ThisProject).value / "webpack/" * "*.js"
}

val webpackDevConfigFilePath = Def.setting {
  Some((baseDirectory in ThisProject).value / "webpack/webpack-dev.config.js")
}

val webpackProdConfigFilePath = Def.setting {
  Some((baseDirectory in ThisProject).value / "webpack/webpack-prod.config.js")
}

lazy val frontend = project
  .settings(commonSettings: _*)
  .settings(
    version in webpack := "4.28.3",
    version in startWebpackDevServer := "3.1.2",
    webpackDevServerExtraArgs := Seq("--watch-content-base"),
    webpackResources := webpackResourcesBlob.value,
    webpackConfigFile in fullOptJS := webpackProdConfigFilePath.value,
    webpackConfigFile in fastOptJS := webpackDevConfigFilePath.value,
    webpackDevServerPort := 9000,
    scalaJSUseMainModuleInitializer := true,
    webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
    webpackBundlingMode in fullOptJS := BundlingMode.Application,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.4",
      "com.github.japgolly.scalajs-react" %%% "extra" % "1.3.1",
      "com.github.japgolly.scalajs-react" %%% "core" % "1.3.1",
      "com.github.japgolly.scalacss" %%% "core" % "0.5.4",
      "com.github.japgolly.scalacss" %%% "ext-react" % "0.5.4",
      "org.typelevel" %%% "cats-effect" % "1.0.0",
      "io.suzaku" %%% "diode" % "1.1.4",
      "io.suzaku" %%% "diode-react" % "1.1.4.131",
      "io.suzaku" %%% "boopickle" % "1.3.0",
      "com.lihaoyi" %%% "autowire" % "0.2.6"
    ),
    npmDependencies in Compile ++= Seq(
      "react" -> "16.5.1",
      "react-dom" -> "16.5.1",
      "victory" -> "32.1.0"
    ),
    npmDevDependencies in Compile ++= Seq(
      "css-loader"                  -> "0.28.9",
      "extract-text-webpack-plugin" -> "4.0.0-beta.0",
      "html-webpack-plugin" -> "3.2.0",
      "node-sass"                   -> "4.7.2",
      "sass-loader"                 -> "6.0.6",
      "style-loader"                -> "0.19.1",
      "webpack-merge"               -> "4.1.1",
      "file-loader"                 -> "1.1.11"
    ))
  .enablePlugins(ScalaJSBundlerPlugin, ScalaJSPlugin)
  .dependsOn(sharedJS)


lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings: _*)
  .settings(
    name := "shared"
  )
  .settings(
    libraryDependencies ++= Seq(
      "io.getquill" %%% "quill-core" % "2.6.0",
      "io.suzaku" %%% "boopickle" % "1.3.0",
    )
  )
  .jvmSettings(
  // Add JVM-specific settings here
  ).jsSettings(
  // Add JS-specific settings here
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js