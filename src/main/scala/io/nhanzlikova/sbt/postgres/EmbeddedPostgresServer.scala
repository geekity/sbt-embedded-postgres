package io.nhanzlikova.sbt.postgres

import java.net.URI

import ru.yandex.qatools.embed.postgresql.{PostgresStarter, PostgresProcess}
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.{ Credentials, Net, Storage, Timeout }
import ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION

import scala.collection.JavaConverters._

class EmbeddedPostgresServer(
  dbUrl: String,
  username: String,
  password: String
) {

  private var process: PostgresProcess = _

  private case class ConnectionConfig(dbUrl: String, username: String, password: String)

  private lazy val connectionConfig = {
    ConnectionConfig(
      dbUrl = dbUrl,
      username = username,
      password = password
    )
  }

  def start(): Unit = {
    val uri = URI.create(connectionConfig.dbUrl.stripPrefix("jdbc:"))
    val port = uri.getPort match {
      case -1 => throw new IllegalArgumentException("Invalid port in connection string")
      case p => p
    }
    val dbName = uri.getPath.stripPrefix("/")

    val config = new PostgresConfig(
      PRODUCTION,
      new Net(uri.getHost, port),
      new Storage(dbName),
      new Timeout(),
      new Credentials(connectionConfig.username, connectionConfig.password)
    )

    // Start with DBNAME arg
    config.getAdditionalInitDbParams.addAll(List(dbName).asJava)

    val runtime = PostgresStarter.getDefaultInstance()
    val exec = runtime.prepare(config)
    process = exec.start
  }

  def stop(): Unit = {
    process.stop
  }

}
