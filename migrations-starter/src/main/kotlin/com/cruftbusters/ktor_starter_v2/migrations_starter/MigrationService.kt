package com.cruftbusters.ktor_starter_v2.migrations_starter

import java.sql.Connection

typealias MigrationVersion = Int

class MigrationService(
  connectionSupplier: () -> Connection,
  private val statements: List<Pair<MigrationVersion, String>>,
  private val name: String = "migration-service",
) {
  init {
    validateStatementVersions()
    validateStatementVersionsIncreasing()
  }

  private fun validateStatementVersions() {
    if (statements.any { (version, _) -> version == Int.MIN_VALUE })
      throw Error("Statements must have version greater than ${Int.MIN_VALUE}")
  }

  private fun validateStatementVersionsIncreasing() {
    statements.windowed(2).forEach { (first, last) ->
      val (version) = first
      val (nextVersion) = last
      if (nextVersion <= version)
        throw Error("Statements versions $version and $nextVersion must be unique and increasing")
    }
  }

  private val connection = connectionSupplier()

  fun migrate() {
    connection.createStatement()
      .execute("create table if not exists migration_sets (name text primary key, version integer)")
    val currentVersion = getCurrentVersion()
    executeMigrations(currentVersion)
    val (version, _) = statements.last()
    if (currentVersion > Int.MIN_VALUE) updateVersion(version)
    else insertVersion(version)
  }

  private fun getCurrentVersion() = connection
    .prepareStatement("select version from migration_sets where name = ?")
    .apply { setString(1, name) }
    .executeQuery().use { resultSet ->
      if (resultSet.next()) resultSet.getInt(1)
      else Int.MIN_VALUE
    }

  private fun executeMigrations(sinceVersion: MigrationVersion) = statements
    .filter { (version, _) -> version > sinceVersion }
    .forEach { (_, statement) -> connection.createStatement().execute(statement) }

  private fun insertVersion(version: MigrationVersion) = connection
    .prepareStatement("insert into migration_sets (name, version) values (?, ?)").apply {
      setString(1, name)
      setInt(2, version)
    }.execute()

  private fun updateVersion(version: MigrationVersion) = connection
    .prepareStatement("update migration_sets set version = ? where name = ?").apply {
      setInt(1, version)
      setString(2, name)
    }.execute()
}