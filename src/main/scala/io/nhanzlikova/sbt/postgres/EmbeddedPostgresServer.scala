package io.nhanzlikova.sbt.postgres

import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import ru.yandex.qatools.embed.postgresql.distribution.Version

class EmbeddedPostgresServer(
  host:String,
  port:Int,
  dbName: String,
  username: String,
  password: String,
  pgVersion: Version.Main
) {
  val pg = new EmbeddedPostgres(pgVersion)

  def start(): Unit =
    if(!pg.getProcess.isPresent)
      pg.start(host,port, dbName,username, password)

  def stop(): Unit = pg.stop()

  def isRunning: Boolean = pg.getProcess.isPresent
}
