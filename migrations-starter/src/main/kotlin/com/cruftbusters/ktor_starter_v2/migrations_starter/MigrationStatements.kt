package com.cruftbusters.ktor_starter_v2.migrations_starter


class MigrationStatements(
  private val statements: List<VersionedStatement>
) {
  class Builder(private val statements: MutableList<VersionedStatement> = mutableListOf()) {
    private var previousVersion: Int? = null
    fun add(version: Int, text: String) {
      if (previousVersion != null && previousVersion!! >= version)
        throw Error("Statements versions $previousVersion and $version must be unique and increasing")
      previousVersion = version
      statements.add(VersionedStatement(version, text))
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