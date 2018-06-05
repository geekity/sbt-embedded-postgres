package io.nhanzlikova.sbt.postgres

import java.sql.Connection
import java.util.Properties

import org.scalatest.{FlatSpec, Matchers}
import ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION

class EmbeddedPostgresServerSpec extends FlatSpec with Matchers {

  val dbPort = 25432
  val dbName = "testdatabase"
  val username = "admin"
  val password = "admin"
  val dbUrl = s"jdbc:postgresql://localhost:${dbPort}/${dbName}?user=${username}&password=${password}"

  val pg = new EmbeddedPostgresServer("localhost", dbPort, dbName, username, password, PRODUCTION)

  "An embedded postgres server" should "create a default database" in {
    pg.start()
    val conn: Connection = new org.postgresql.Driver().connect(dbUrl, new Properties())

    val query = conn.createStatement().executeQuery("SELECT count(*) FROM pg_database where datname='testdatabase'")
    while (query.next()) query.getInt("count") shouldBe 1

    conn.close()
    pg.stop()
  }

}
