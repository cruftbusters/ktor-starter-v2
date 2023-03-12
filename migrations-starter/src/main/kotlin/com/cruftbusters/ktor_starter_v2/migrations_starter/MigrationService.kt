package com.cruftbusters.ktor_starter_v2.migrations_starter

import org.slf4j.Logger
import java.sql.Connection

typealias MigrationVersion = Int

class MigrationService(
  private val connectionSupplier: () -> Connection,
  private val logger: Logger,
  private val name: String = "migration-service",
  private val block: MigrationStatements.Builder.() -> Unit,
) {
  private val statements: MigrationStatements = MigrationStatements.Builder().apply(block).build()
  fun migrate() = connectionSupplier().use { migrate(it) }

  private fun migrate(connection: Connection) {
    connection.createStatement()
      .execute("create table if not exists migration_sets (name text primary key, version integer)")

    val currentVersion = getCurrentVersion(connection)
    if (currentVersion != null) {
      executeMigrations(connection, statements.since(currentVersion))
      updateVersion(connection, statements.latestVersion)
    } else {
      executeMigrations(connection, statements.all)
      insertVersion(connection, statements.latestVersion)
    }
  }

  private fun getCurrentVersion(connection: Connection) = connection
    .prepareStatement("select version from migration_sets where name = ?")
    .apply { setString(1, name) }
    .executeQuery().use { resultSet ->
      if (resultSet.next()) resultSet.getInt(1)
      else null
    }

  private fun executeMigrations(connection: Connection, statements: List<String>) = statements.forEach { statement ->
    logger.info("applied: '$statement'")
    connection.createStatement().execute(statement)
  }

  private fun insertVersion(connection: Connection, version: MigrationVersion) = connection
    .prepareStatement("insert into migration_sets (name, version) values (?, ?)").apply {
      setString(1, name)
      setInt(2, version)
    }.execute()

  private fun updateVersion(connection: Connection, version: MigrationVersion) = connection
    .prepareStatement("update migration_sets set version = ? where name = ?").apply {
      setInt(1, version)
      setString(2, name)
    }.execute()
}