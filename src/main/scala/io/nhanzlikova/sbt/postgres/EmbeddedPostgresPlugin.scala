package io.nhanzlikova.sbt.postgres

import ru.yandex.qatools.embed.postgresql.distribution.Version
import ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION
import sbt.Keys._
import sbt._

object EmbeddedPostgresPlugin extends AutoPlugin {

  override val trigger = noTrigger

  object autoImport {
    val postgresPort = settingKey[Int]("Postgres server port")
    val postgresDatabase = settingKey[String]("Postgres database name")
    val postgresConnectionString = settingKey[String]("Postgres connection string.")
    val postgresUsername = settingKey[String]("Postgres username.")
    val postgresPassword = settingKey[String]("Postgres password.")
    val postgresVersion = settingKey[Version.Main]("Postgres version")
    val postgresSilencer = settingKey[Boolean]("suppress output from the embedded postgres at start and stop")

    val startPostgres = taskKey[Unit]("start-postgres")
    val stopPostgres = taskKey[Unit]("stop-postgres")
    val postgresServer = settingKey[EmbeddedPostgresServer]("Postgres server")
    val postgresTestCleanup = TaskKey[Tests.Cleanup]("postgres-test-cleanup")
    val postgresIsRunning = taskKey[Boolean]("postgres-is-running")
  }

  import autoImport._

  private def silenceOutput[A](op: => A): A = {
    import java.io.{OutputStream, PrintStream}

    val oldOut = System.out
    System.setOut(new PrintStream(new OutputStream {
      override def write(i: Int): Unit = ()
    }))
    val res = op
    System.setOut(oldOut)
    res
  }

  def defaultSettings = Seq(
    postgresPort := 25432,
    postgresDatabase := "database",
    postgresConnectionString := s"jdbc:postgresql://localhost:${postgresPort.value}/${postgresDatabase.value}",
    postgresUsername := "admin",
    postgresPassword := "admin",
    postgresVersion := PRODUCTION,
    postgresSilencer := false,
    postgresServer := new EmbeddedPostgresServer(
      "localhost",
      postgresPort.value,
      postgresDatabase.value,
      postgresUsername.value,
      postgresPassword.value,
      postgresVersion.value
    )
  )

  def tasks = Seq(
    startPostgres := {
      streams.value.log.info(s"Starting Postgres on ${postgresConnectionString.value} ...")
      if (postgresSilencer.value)
        silenceOutput(postgresServer.value.start())
      else
        postgresServer.value.start()
      streams.value.log.info("Postgres started")
    },
    stopPostgres := {
      streams.value.log.info("Stopping Postgres...")
      if (postgresSilencer.value)
        silenceOutput(postgresServer.value.stop())
      else
        postgresServer.value.stop()
      streams.value.log.info("Postgres stopped")
    },
    //make sure to Stop Postgres when tests are done.
    postgresTestCleanup := Tests.Cleanup(() => postgresServer.value.stop()),
    postgresIsRunning := postgresServer.value.isRunning
  )

  override val projectSettings = defaultSettings ++ tasks
}
