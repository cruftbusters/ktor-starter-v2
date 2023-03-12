package com.cruftbusters.ktor_starter_v2.migrations_starter


class MigrationStatements(
  private val statements: List<VersionedStatement>
) {
  class Builder(private val statements: MutableList<VersionedStatement> = mutableListOf()) {
    fun add(version: Int, statement: String) {
      val lastVersion = statements.map { it.version }.lastOrNull()
      if (lastVersion != null && lastVersion >= version)
        throw Error("Statements versions $lastVersion and $version must be unique and increasing")
      statements.add(VersionedStatement(version, statement))
    }

    fun build() = MigrationStatements(statements)
  }

  data class VersionedStatement(val version: Int, val statement: String)

  fun since(version: Int): List<String> = statements
    .filter { it.version > version }
    .map { it.statement }

  val all = statements.map { it.statement }

  val latestVersion = statements.maxOf { it.version }
}