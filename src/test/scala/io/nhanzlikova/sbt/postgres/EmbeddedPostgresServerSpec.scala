package io.nhanzlikova.sbt.postgres

import java.sql.{DriverManager, Connection}

import org.scalatest.{Matchers, BeforeAndAfter, FlatSpec}
import ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION

class EmbeddedPostgresServerSpec extends FlatSpec with Matchers with BeforeAndAfter {

  val dbPort = 25432
  val dbName = "testdatabase"
  val dbUrl = s"jdbc:postgresql://localhost:${dbPort}/${dbName}"
  val username = "admin"
  val password = "admin"

  val pg = new EmbeddedPostgresServer("localhost", dbPort, dbName, username, password, PRODUCTION)

  before {
    pg.start()
  }

  after {
    pg.stop()
  }

  "An embedded postgres server" should "create a default database" in {
    val conn: Connection = {
      DriverManager.getConnection(
        dbUrl, username, password
      )
    }

    val query = conn.createStatement().executeQuery("SELECT count(*) FROM pg_database where datname='testdatabase'")
    while (query.next()) query.getInt("count") shouldBe 1

    conn.close()
  }

}
