package com.red.repository.transactions

import com.red.models.Transaction
import com.red.models.TransactionElement
import com.red.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.LocalDateTime

class TransactionRepository : ITransactionRepository {

    override suspend fun addTransaction(transaction: Transaction): Transaction? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Transactions.insert {
                it[Transactions.primaryKey] = "${transaction.userId}_${transaction.id}"
                it[Transactions.id] = transaction.id ?: 0
                it[Transactions.userId] = transaction.userId ?: 0
                it[Transactions.name] = transaction.name ?: ""
                it[Transactions.categories] = transaction.categories
                it[Transactions.date] = transaction.date ?: LocalDateTime.now()
                it[Transactions.type] = transaction.type ?: "MAIN"
                it[Transactions.elements] = convertElementsToString(transaction.elements)
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

    override suspend fun deleteTransactions(userId: Int): Boolean {
        val deletedRowNumber = dbQuery {
            Transactions.deleteWhere { Transactions.userId.eq(userId) }
        }
        return deletedRowNumber > 0
    }

    override suspend fun updateTransaction(userId: Int, transaction: Transaction): Boolean {

        val updatedRows = dbQuery {
            Transactions.update(
                where = { Transactions.userId.eq(userId) and Transactions.id.eq(transaction.id ?: -1) },
                body =
                {
                    it[Transactions.name] = transaction.name ?: ""
                    it[Transactions.categories] = transaction.categories
                    it[Transactions.date] = transaction.date ?: LocalDateTime.now()
                    it[Transactions.type] = transaction.type ?: "MAIN"
                    it[Transactions.elements] = convertElementsToString(transaction.elements)
                    it[Transactions.currency] = transaction.currency ?: ""
                    it[Transactions.totalCost] = transaction.totalCost ?: 0.0
                })
        }

        return updatedRows > 0
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
            elements = convertToElements(row[Transactions.elements]),
            currency = row[Transactions.currency],
            totalCost = row[Transactions.totalCost],
        )
    }

    private fun convertToElements(elementsString: String?): List<TransactionElement>? {
        elementsString ?: return null


        val splitedByElements = elementsString.split(";")
        val cleanedByClassName = splitedByElements.map {
            it.subSequence(19, it.lastIndex)
        }
        val elements = cleanedByClassName.map { allParam ->
            val params = allParam.split(",").map { paramWithValue ->
                val separatedParamValue = paramWithValue.trim().split("=")
                separatedParamValue[0] to separatedParamValue[1]
            }

            val id = params.find { it.first == "id" }?.second?.toLong() ?: 0
            val userId = params.find { it.first == "userId" }?.second?.toInt() ?: -1
            val name = params.find { it.first == "name" }?.second ?: ""
            val cost = params.find { it.first == "cost" }?.second?.toDouble() ?: 0.0
            val categoryListText = params.find { it.first == "categories" }?.second

            var categories = emptyList<Int>()
            if (categoryListText != null && categoryListText != "[]") {
                val cleaned = categoryListText.substring(1, categoryListText.lastIndex)
                categories = cleaned.split(",").map { it.toInt() }
            }
            TransactionElement(
                id = id,
                userId = userId,
                name = name,
                cost = cost,
                categories = categories
            )
        }
        return elements
    }

    private fun convertElementsToString(list: List<TransactionElement>?): String? {
        list ?: return null
        var result = ""
        list.forEachIndexed { i, element ->
            result += element.toString()
            if (i != list.lastIndex) {
                result += ";"
            }
        }
        return result
    }
}