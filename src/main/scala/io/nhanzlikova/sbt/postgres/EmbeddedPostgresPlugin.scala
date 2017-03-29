package io.nhanzlikova.sbt.postgres

import sbt._
import sbt.Keys._

object EmbeddedPostgresPlugin extends AutoPlugin {

  override val trigger = allRequirements

  object autoImport {

    val postgresConnectionString = settingKey[String]("Postgres connection string.")
    val postgresPort = settingKey[Int]("Postgres port.")
    val postgresUsername = settingKey[String]("Postgres username.")
    val postgresPassword = settingKey[String]("Postgres password.")

    val startPostgres = taskKey[Unit]("start-postgres")
    val stopPostgresAfterTests = settingKey[Boolean]("stop-postgres-after-tests")
    val stopPostgres = taskKey[Unit]("stop-postgres")
    val postgresServer = settingKey[EmbeddedPostgresServer]("Postgres server")
    val postgresTestCleanup = TaskKey[Tests.Cleanup]("postgres-test-cleanup")
  }

  import autoImport._

  override val projectSettings = Seq(
    postgresConnectionString := "jdbc:postgresql://localhost:25432/database",
    postgresPort := 25432,
    postgresUsername := "admin",
    postgresPassword := "admin",
    stopPostgresAfterTests := true,
    postgresServer := new EmbeddedPostgresServer(
      postgresConnectionString.value,
      postgresPort.value,
      postgresUsername.value,
      postgresPassword.value
    ),
    startPostgres := {
      streams.value.log.info("Starting Postgres...")
      postgresServer.value.start()
      ()
    },
    //if compilation of test classes fails, Postgres should not be invoked. (moreover, Test.Cleanup won't execute to stop it...)
    startPostgres <<= startPostgres.dependsOn(compile in Test),
    stopPostgres := {
      streams.value.log.info("Stopping Postgres...")
      postgresServer.value.stop()
      ()
    },
    //make sure to Stop Postgres when tests are done.
    postgresTestCleanup := Tests.Cleanup(() => {
      streams.value.log.info("Stopping Postgres...")
      postgresServer.value.stop()
    })
  )
}