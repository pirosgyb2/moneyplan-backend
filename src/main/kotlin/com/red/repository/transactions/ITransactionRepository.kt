package com.red.repository.transactions

import com.red.models.Transaction

interface ITransactionRepository {

    suspend fun addTransaction(transaction: Transaction): Transaction?
    suspend fun getTransactions(userId: Int): List<Transaction>

}