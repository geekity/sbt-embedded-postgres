package io.nhanzlikova.sbt.postgres

import java.sql.{DriverManager, Connection}

import org.scalatest.{Matchers, BeforeAndAfter, FlatSpec}

class EmbeddedPostgresServerSpec extends FlatSpec with Matchers with BeforeAndAfter {

  val dbUrl = "jdbc:postgresql://localhost:25432/testdatabase"
  val username = "admin"
  val password = "admin"

  val pg = new EmbeddedPostgresServer(dbUrl, username, password)

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
