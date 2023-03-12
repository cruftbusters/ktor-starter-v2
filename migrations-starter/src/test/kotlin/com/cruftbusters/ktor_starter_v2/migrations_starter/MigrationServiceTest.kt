package com.cruftbusters.ktor_starter_v2.migrations_starter

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MigrationServiceTest : FunSpec({
  val dataSource = HikariDataSource(HikariConfig().apply { jdbcUrl = "jdbc:h2:mem:migration-service-test" })
  context("initial migration") {
    val service = MigrationService(
      dataSource::getConnection,
      MigrationStatements(
        Pair(1, "create table demo (id text primary key, document text)"),
      ),
    )
    test("should migrate without fail") {
      service.migrate()
    }
    test("should be able to utilize migrations") {
      val connection = dataSource.connection
      connection.createStatement().execute("insert into demo (id, document) values ('the id', 'the document')")
      connection.createStatement().executeQuery("select id, document from demo").use { resultSet ->
        resultSet.next()
        resultSet.getString(1) shouldBe "the id"
        resultSet.getString(2) shouldBe "the document"
      }
    }
  }
  test("should not raise error when re-applying migrations") {
    MigrationService(
      dataSource::getConnection,
      MigrationStatements(
        Pair(1, "create table demo (id text primary key, document text)"),
      ),
    ).migrate()
  }
  context("updated migration") {
    val service = MigrationService(
      dataSource::getConnection,
      MigrationStatements(
        Pair(1, "create table demo (id text primary key, document text)"),
        Pair(2, "alter table demo add column another_document text"),
      ),
    )
    test("should migrate without fail") {
      service.migrate()
    }
    test("should be able to utilize migrations") {
      val connection = dataSource.connection
      connection.createStatement()
        .execute("insert into demo (id, document, another_document) values ('another id', 'the document', 'another document')")
      connection.createStatement()
        .executeQuery("select id, document, another_document from demo where id = 'another id'").use { resultSet ->
          resultSet.next()
          resultSet.getString(1) shouldBe "another id"
          resultSet.getString(2) shouldBe "the document"
          resultSet.getString(3) shouldBe "another document"
        }
    }
  }
})