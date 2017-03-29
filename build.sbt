name := "sbt-embedded-postgres"

organization := "io.nhanzlikova.sbt"

version := "git describe --tags --dirty --always".!!.stripPrefix("v").trim

sbtPlugin := true

scalacOptions ++= List("-unchecked")

publishMavenStyle := false
bintrayRepository := "sbt-plugins"
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

libraryDependencies ++= {
  Seq(
    "ru.yandex.qatools.embed" % "postgresql-embedded" % "1.15",
    "org.postgresql" % "postgresql" % "9.4.1209",
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )
}
