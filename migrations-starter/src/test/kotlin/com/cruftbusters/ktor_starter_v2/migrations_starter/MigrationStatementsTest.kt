package com.cruftbusters.ktor_starter_v2.migrations_starter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MigrationStatementsTest : FunSpec({
  test("should raise error when statement versions are non-increasing") {
    val error = shouldThrow<Error> {
      MigrationStatements(
        MigrationStatement(7, "doesnt matter"),
        MigrationStatement(8, "doesnt matter"),
        MigrationStatement(8, "doesnt matter"),
      )
    }
    error.message shouldBe "Statements versions 8 and 8 must be unique and increasing"
  }
  test("get statements since version") {
    MigrationStatements(
      MigrationStatement(1, "skip me"),
      MigrationStatement(2, "include me"),
      MigrationStatement(3, "include me too"),
    ).since(1) shouldBe listOf(
      "include me",
      "include me too",
    )
  }
  test("get all statements") {
    MigrationStatements(
      MigrationStatement(1, "dont skip me"),
      MigrationStatement(2, "include me"),
      MigrationStatement(3, "include me too"),
    ).all shouldBe listOf(
      "dont skip me",
      "include me",
      "include me too",
    )
  }
  test("get latest version") {
    MigrationStatements(
      MigrationStatement(1, "first"),
      MigrationStatement(2, "second"),
      MigrationStatement(3, "third"),
    ).latestVersion shouldBe 3
  }
})