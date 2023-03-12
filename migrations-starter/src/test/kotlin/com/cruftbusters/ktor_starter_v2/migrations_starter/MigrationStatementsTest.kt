package com.cruftbusters.ktor_starter_v2.migrations_starter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MigrationStatementsTest : FunSpec({
  test("should raise error when statement versions are non-increasing") {
    val error = shouldThrow<Error> {
      MigrationStatements.Builder().apply {
        add(7, "doesnt matter")
        add(8, "doesnt matter")
        add(8, "doesnt matter")
      }.build()
    }
    error.message shouldBe "Statements versions 8 and 8 must be unique and increasing"
  }
  test("get statements since version") {
    MigrationStatements.Builder().apply {
      add(1, "skip me")
      add(2, "include me")
      add(3, "include me too")
    }.build().since(1) shouldBe listOf(
      "include me",
      "include me too",
    )
  }
  test("get all statements") {
    MigrationStatements.Builder().apply {
      add(1, "dont skip me")
      add(2, "include me")
      add(3, "include me too")
    }.build().all shouldBe listOf(
      "dont skip me",
      "include me",
      "include me too",
    )
  }
  test("get latest version") {
    MigrationStatements.Builder().apply {
      add(1, "first")
      add(2, "second")
      add(3, "third")
    }.build().latestVersion shouldBe 3
  }
})