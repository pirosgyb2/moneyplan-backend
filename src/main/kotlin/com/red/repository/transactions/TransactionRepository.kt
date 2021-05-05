package com.red.repository.transactions

import com.red.models.Transaction
import com.red.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.LocalDateTime

class TransactionRepository : ITransactionRepository {

    override suspend fun addTransaction(transaction: Transaction): Transaction? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Transactions.insert {
                it[Transactions.userId] = transaction.userId ?: 0
                it[Transactions.id] = transaction.id ?: 0
                it[Transactions.name] = transaction.name ?: ""
                it[Transactions.categories] = transaction.categories
                it[Transactions.date] = transaction.date ?: LocalDateTime.now()
                it[Transactions.type] = transaction.type ?: "MAIN"
                it[Transactions.elements] = transaction.elements
                it[Transactions.currency] = transaction.currency ?: ""
                it[Transactions.totalCost] = transaction.totalCost ?: 0.0
            }
        }
        return rowToTransaction(statement?.resultedValues?.get(0))
    }


    override suspend fun getTransactions(userId: Int): List<Transaction> {
        return dbQuery {
            Transactions.select {
                Transactions.userId.eq(userId)
            }.mapNotNull { rowToTransaction(it) }
        }
    }

    override suspend fun deleteTransaction(userId: Int, transactionId: Int): Boolean {
        val deletedRowNumber = dbQuery {
            Transactions.deleteWhere {
                Transactions.userId.eq(userId) and Transactions.id.eq(transactionId)
            }
        }
        return deletedRowNumber > 0
    }


    private fun rowToTransaction(row: ResultRow?): Transaction? {
        if (row == null) {
            return null
        }
        return Transaction(
            id = row[Transactions.id],
            userId = row[Transactions.userId],
            name = row[Transactions.name],
            categories = row[Transactions.categories],
            date = row[Transactions.date],
            type = row[Transactions.type],
            elements = row[Transactions.elements],
            currency = row[Transactions.currency],
            totalCost = row[Transactions.totalCost],
        )
    }
}