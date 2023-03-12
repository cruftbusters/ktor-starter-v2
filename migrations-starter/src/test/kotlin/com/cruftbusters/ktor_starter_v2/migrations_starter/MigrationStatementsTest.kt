package com.cruftbusters.ktor_starter_v2.migrations_starter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MigrationStatementsTest : FunSpec({
  test("should raise error when statement versions are non-increasing") {
    val error = shouldThrow<Error> {
      MigrationStatements(
        Pair(7, "doesnt matter"),
        Pair(8, "doesnt matter"),
        Pair(8, "doesnt matter"),
      )
    }
    error.message shouldBe "Statements versions 8 and 8 must be unique and increasing"
  }
  test("get statements since version") {
    MigrationStatements(
      Pair(1, "skip me"),
      Pair(2, "include me"),
      Pair(3, "include me too"),
    ).since(1) shouldBe listOf(
      "include me",
      "include me too",
    )
  }
  test("get all statements") {
    MigrationStatements(
      Pair(1, "dont skip me"),
      Pair(2, "include me"),
      Pair(3, "include me too"),
    ).all() shouldBe listOf(
      "dont skip me",
      "include me",
      "include me too",
    )
  }
  test("get latest version") {
    MigrationStatements(
      Pair(1, "first"),
      Pair(2, "second"),
      Pair(3, "third"),
    ).latestVersion shouldBe 3
  }
})