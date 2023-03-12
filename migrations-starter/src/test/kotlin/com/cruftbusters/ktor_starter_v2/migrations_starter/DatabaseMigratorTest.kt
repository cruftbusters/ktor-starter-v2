package com.cruftbusters.ktor_starter_v2.migrations_starter

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.slf4j.Logger

class DatabaseMigratorTest : FunSpec({
  val dataSource = HikariDataSource(HikariConfig().apply { jdbcUrl = "jdbc:h2:mem:migration-service-test" })
  val logger = mockk<Logger>(relaxUnitFun = true)
  context("initial migration") {
    val migrator = DatabaseMigrator(dataSource::getConnection, logger) {
      add(1, "create table demo (id text primary key, document text)")
    }
    test("should migrate without fail") {
      migrator.migrate()
      verify { logger.info("applied: 'create table demo (id text primary key, document text)'") }
    }
    test("should be able to utilize migrations") {
      dataSource.connection.use {
        it.createStatement().execute("insert into demo (id, document) values ('the id', 'the document')")
        it.createStatement().executeQuery("select id, document from demo").use { resultSet ->
          resultSet.next()
          resultSet.getString(1) shouldBe "the id"
          resultSet.getString(2) shouldBe "the document"
        }
      }
    }
  }
  test("should not raise error when re-applying migrations") {
    DatabaseMigrator(dataSource::getConnection, logger) {
      add(1, "create table demo (id text primary key, document text)")
    }.migrate()
  }
  context("updated migration") {
    val service = DatabaseMigrator(dataSource::getConnection, logger) {
      add(1, "create table demo (id text primary key, document text)")
      add(2, "alter table demo add column another_document text")
    }
    test("should migrate without fail") {
      service.migrate()
      verify { logger.info("applied: 'alter table demo add column another_document text'") }
    }
    test("should be able to utilize migrations") {
      dataSource.connection.use {
        it.createStatement()
          .execute("insert into demo (id, document, another_document) values ('another id', 'the document', 'another document')")
        it.createStatement()
          .executeQuery("select id, document, another_document from demo where id = 'another id'").use { resultSet ->
            resultSet.next()
            resultSet.getString(1) shouldBe "another id"
            resultSet.getString(2) shouldBe "the document"
            resultSet.getString(3) shouldBe "another document"
          }
      }
    }
  }
})