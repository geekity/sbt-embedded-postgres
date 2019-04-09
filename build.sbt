name := "sbt-embedded-postgres"

organization := "io.nhanzlikova.sbt"

version := sys.process.Process("git describe --tags --dirty --always").lineStream_!.head.stripPrefix("v").trim

sbtPlugin := true

scalacOptions ++= List("-unchecked")

publishMavenStyle := false
bintrayRepository := "sbt-plugins"
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

libraryDependencies ++= {
  Seq(
    "ru.yandex.qatools.embed" % "postgresql-embedded" % "2.10",
    "org.postgresql" % "postgresql" % "42.2.5"  % Test,
    "org.scalatest" %% "scalatest" % "3.0.7" % Test
  )
}
