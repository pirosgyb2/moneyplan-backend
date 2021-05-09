package com.red.repository.transactions

import com.red.models.Transaction

interface ITransactionRepository {

    suspend fun addTransaction(transaction: Transaction): Transaction?
    suspend fun getTransactions(userId: Int): List<Transaction>
    suspend fun deleteTransaction(userId: Int, transactionId: Int): Boolean
    suspend fun deleteTransactions(userId: Int): Boolean
    suspend fun updateTransaction(userId: Int, transaction: Transaction): Boolean

}