package com.cruftbusters.ktor_starter_v2.migrations_starter

typealias MigrationStatement = Pair<Int, String>

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
    .filter { (statementVersion, _) -> statementVersion > version }
    .map { it.second }

  fun all() = statements.map { it.second }

  val latestVersion = statements.maxOf { it.first }
}