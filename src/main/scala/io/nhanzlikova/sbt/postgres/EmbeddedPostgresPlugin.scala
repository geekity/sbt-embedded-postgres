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
    val postgresUsername = settingKey[String]("Postgres username.")
    val postgresPassword = settingKey[String]("Postgres password.")
    val postgresVersion = settingKey[Version.Main]("Postgres version")
    val postgresSilencer = settingKey[Boolean]("suppress output from the embedded postgres at start and stop")

    val postgresConnectionString = taskKey[String]("Postgres connection string.")
    val startPostgres = taskKey[String]("start-postgres")
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

  def isReachable(host: String, port: Int, timeout: Int = 2000): Boolean = {
    import java.io.IOException
    import java.net.{InetSocketAddress, Socket}

    val socket = new Socket
    try {
      socket.connect(new InetSocketAddress(host, port), timeout)
      true
    } catch {
      case _: IOException => false
    } finally {
      socket.close()
    }
  }

  def getFreePort(range: IndexedSeq[Int]): Int =
    scala.util.Random.shuffle(range).find(x => !isReachable("localhost", x)).getOrElse {
      throw new RuntimeException(s"No free port available in given port range.")
    }

  def defaultSettings = Seq(
    postgresPort := 25432,
    postgresDatabase := "database",
    postgresConnectionString := startPostgres.value,
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
      streams.value.log.info(s"Starting Postgres ...")
      val server = postgresServer.value
      if (postgresSilencer.value)
        silenceOutput(server.start())
      else
        server.start()
      val connectionString = server.pg.getConnectionUrl.get
      streams.value.log.info(s"Postgres started on ... $connectionString")
      connectionString
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
