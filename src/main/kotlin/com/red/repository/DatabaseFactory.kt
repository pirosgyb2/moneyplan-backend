package com.red.repository

import com.red.repository.categories.Categories
import com.red.repository.goals.Goals
import com.red.repository.transactions.Transactions
import com.red.repository.users.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Transactions)
            SchemaUtils.create(Categories)
            SchemaUtils.create(Goals)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.jdbcUrl = convertdatabseUrlToJDBSScheme(System.getenv("DATABASE_URL"))
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        config.username = getUsername(System.getenv("DATABASE_URL"))
        config.password = getPassword(System.getenv("DATABASE_URL"))

        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

    private fun convertdatabseUrlToJDBSScheme(hikariUrl: String): String {
        val withoutHead = hikariUrl.drop(11)
        val credentialsAndUrl = withoutHead.split('@')
        val url = credentialsAndUrl[1]

        val credentials = credentialsAndUrl[0].split(':')
        val username = credentials[0]
        val password = credentials[1]

        return "jdbc:postgresql://$url?user=$username&password=$password"
    }

    private fun getUsername(hikariUrl: String): String{
        val withoutHead = hikariUrl.drop(11)
        val credentialsAndUrl = withoutHead.split('@')
        val credentials = credentialsAndUrl[0].split(':')
        val username = credentials[0]
        return username
    }

    private fun getPassword(hikariUrl: String): String{
        val withoutHead = hikariUrl.drop(11)
        val credentialsAndUrl = withoutHead.split('@')
        val credentials = credentialsAndUrl[0].split(':')
        val password = credentials[1]
        return password
    }
}