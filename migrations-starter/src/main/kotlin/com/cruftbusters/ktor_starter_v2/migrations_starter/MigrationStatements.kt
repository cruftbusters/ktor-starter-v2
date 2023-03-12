package com.cruftbusters.ktor_starter_v2.migrations_starter


class MigrationStatements(
  block: Builder.() -> Unit,
) {
  private val statements = Builder().apply(block).statements

  class Builder(val statements: MutableList<VersionedStatement> = mutableListOf()) {
    fun add(version: Int, statement: String) {
      statements.add(VersionedStatement(version, statement))
    }
  }

  data class VersionedStatement(val version: Int, val statement: String)

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