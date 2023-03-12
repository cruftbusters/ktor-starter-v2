package com.cruftbusters.ktor_starter_v2.migrations_starter

data class MigrationStatement(
  val version: Int,
  val statement: String,
)

class MigrationStatements(
  private vararg val statements: MigrationStatement,
) {
  init {
    statements.toList().windowed(2).forEach { (first, last) ->
      val (version) = first
      val (nextVersion) = last
      if (nextVersion <= version)
        throw Error("Statements versions $version and $nextVersion must be unique and increasing")
    }
  }

  fun since(version: Int): List<String> = statements
    .filter { it.version > version }
    .map { it.statement }

  val all = statements.map { it.statement }

  val latestVersion = statements.maxOf { it.version }
}