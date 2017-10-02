package io.nhanzlikova.sbt.postgres

import java.net.URI

import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.{Credentials, Net, Storage, Timeout}
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig
import ru.yandex.qatools.embed.postgresql.distribution.Version
import ru.yandex.qatools.embed.postgresql.{PostgresProcess, PostgresStarter}

import scala.collection.JavaConverters._

class EmbeddedPostgresServer(
  host:String,
  port:Int,
  dbName: String,
  username: String,
  password: String,
  pgVersion: Version.Main
) {

  private var process: Option[PostgresProcess] = None

  def start(): Unit = {
    if(process.isEmpty){
      val config = new PostgresConfig(
        pgVersion,
        new Net(host, port),
        new Storage(dbName),
        new Timeout(),
        new Credentials(username, password)
      )

      config.getAdditionalInitDbParams.addAll(List(
        "-E", "UTF-8",
        "--locale=en_US.UTF-8",
        "--lc-collate=en_US.UTF-8",
        "--lc-ctype=en_US.UTF-8"
      ).asJava)

      process = Some(PostgresStarter.getDefaultInstance.prepare(config).start)
    }
  }

  def stop(): Unit = {
    process.foreach(_.stop())
    process = None
  }

  def isRunning: Boolean = process.nonEmpty
}
