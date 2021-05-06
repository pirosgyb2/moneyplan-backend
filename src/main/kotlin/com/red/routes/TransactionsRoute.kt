package com.red.routes

import com.red.API_VERSION
import com.red.auth.MySession
import com.red.models.Transaction
import com.red.repository.transactions.TransactionRepository
import com.red.repository.users.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val TRANSACTION = "$API_VERSION/transaction"
const val TRANSACTIONS = "$API_VERSION/transactions"

@KtorExperimentalLocationsAPI
@Location(TRANSACTION)
class TransactionRoute

@KtorExperimentalLocationsAPI
@Location(TRANSACTIONS)
class TransactionsRoute

@KtorExperimentalLocationsAPI
fun Route.transactions(transactionRepository: TransactionRepository, userRepository: UserRepository) {
    authenticate("jwt") {
        post<TransactionRoute> {
            val userId = call.getUserId(userRepository) ?: return@post

            val transaction = call.receive<Transaction>()
            val validatedTransaction = call.validateTransaction(transaction, userId) ?: return@post

            try {
                val currentTransaction = transactionRepository.addTransaction(validatedTransaction)
                currentTransaction?.id?.let {
                    call.respond(HttpStatusCode.OK, currentTransaction)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add transaction", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving transaction")
            }
        }

        post<TransactionsRoute> {
            val userId = call.getUserId(userRepository) ?: return@post

            val transactions = call.receive<Array<Transaction>>()
            val savedTransactions = ArrayList<Transaction>()

            transactions.forEach { transaction ->

                val validatedTransaction = call.validateTransaction(transaction, userId) ?: return@post

                try {
                    val currentTransaction = transactionRepository.addTransaction(validatedTransaction)
                    if (currentTransaction?.id == null) {
                        throw Exception("Failed to save transaction")
                    } else {
                        savedTransactions.add(currentTransaction)
                    }
                } catch (e: Throwable) {
                    application.log.error("Failed to add transactions", e)
                    call.respond(HttpStatusCode.BadRequest, "Problems Saving transactions")
                }
            }

            call.respond(HttpStatusCode.OK, savedTransactions)
        }

        get<TransactionsRoute> {
            val userId = call.getUserId(userRepository) ?: return@get

            try {
                val transactions = transactionRepository.getTransactions(userId)
                call.respond(transactions)
            } catch (e: Throwable) {
                application.log.error("Failed to get transactions", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting transactions")
            }
        }

        delete<TransactionRoute> {
            val userId = call.getUserId(userRepository) ?: return@delete

            val params = call.receive<Parameters>()
            val transactionId = params["transactionId"]?.toInt()
            if (transactionId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing transaction id")
                return@delete
            }

            try {
                val isSuccessful = transactionRepository.deleteTransaction(userId, transactionId)
                if (isSuccessful) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Cannot delete transaction")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to delete transaction", e)
                call.respond(HttpStatusCode.BadRequest, "Problems deleting transactions")
            }
        }

        put<TransactionRoute> {
            val userId = call.getUserId(userRepository) ?: return@put

            val transaction = call.receive<Transaction>()
            val validatedTransaction = call.validateTransaction(transaction, userId) ?: return@put

            try {
                val isSuccessful = transactionRepository.updateTransaction(userId, validatedTransaction)
                if (isSuccessful) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems updating transaction")
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add transaction", e)
                call.respond(HttpStatusCode.BadRequest, "Problems updating transaction")
            }
        }

    }
}

private suspend fun ApplicationCall.getUserId(userRepository: UserRepository): Int? {
    val user = sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
    if (user == null) {
        respond(HttpStatusCode.Unauthorized)
    }
    return user?.userId
}

private suspend fun ApplicationCall.validateTransaction(transaction: Transaction, userId: Int): Transaction? {
    if (transaction.id == null) {
        respond(HttpStatusCode.BadRequest, "Missing id")
        return null
    }

    if (transaction.userId == null) {
        transaction.userId = userId
    }

    if (transaction.type == null) {
        respond(HttpStatusCode.BadRequest, "Missing type")
        return null
    }

    return transaction
}