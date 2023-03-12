package com.cruftbusters.ktor_starter_v2.migrations_starter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.sql.DriverManager

class MigrationServiceTest : FunSpec({
  val connection = DriverManager.getConnection("jdbc:h2:mem:migration-service-test")
  test("should raise error when statement version not greater than MIN_VALUE") {
    val error = shouldThrow<Error> {
      MigrationService(connection, listOf(Pair(Int.MIN_VALUE, "doesnt matter"))).migrate()
    }
    error.message shouldBe "Statements must have version greater than -2147483648"
  }
  test("should raise error when statement versions are non-increasing") {
    val error = shouldThrow<Error> {
      MigrationService(
        connection, listOf(
          Pair(8, "doesnt matter"),
          Pair(8, "doesnt matter"),
        )
      ).migrate()
    }
    error.message shouldBe "Statements versions 8 and 8 must be unique and increasing"
  }
  context("execute migrations") {
    val service = MigrationService(
      connection,
      listOf(
        Pair(Int.MIN_VALUE + 1, "create table demo (id text primary key, document text)")
      ),
    )
    test("should migrate without fail") {
      service.migrate()
    }
    test("should be able to utilize migrations") {
      connection.createStatement().execute("insert into demo (id, document) values ('the id', 'the document')")
      connection.createStatement().executeQuery("select id, document from demo").use { resultSet ->
        resultSet.next()
        resultSet.getString(1) shouldBe "the id"
        resultSet.getString(2) shouldBe "the document"
      }
    }
    test("should not raise error from idempotent migrate") {
      service.migrate()
    }
  }
})