package com.cruftbusters.ktor_starter_v2.migrations_starter

import java.sql.Connection

typealias MigrationVersion = Int

class MigrationService(
  connectionSupplier: () -> Connection,
  private val statements: MigrationStatements,
  private val name: String = "migration-service",
) {
  private val connection = connectionSupplier()

  fun migrate() {
    val currentVersion = getCurrentVersion()
    if (currentVersion != null) {
      executeMigrations(statements.since(currentVersion))
      updateVersion(statements.latestVersion)
    } else {
      executeMigrations(statements.all())
      insertVersion(statements.latestVersion)
    }
  }

  private fun getCurrentVersion(): Int? {
    connection.createStatement()
      .execute("create table if not exists migration_sets (name text primary key, version integer)")
    return connection
      .prepareStatement("select version from migration_sets where name = ?")
      .apply { setString(1, name) }
      .executeQuery().use { resultSet ->
        if (resultSet.next()) resultSet.getInt(1)
        else null
      }
  }

  private fun executeMigrations(statements: List<String>) = statements
    .forEach { statement -> connection.createStatement().execute(statement) }

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