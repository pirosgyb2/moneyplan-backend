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

const val TRANSACTIONS = "$API_VERSION/transactions"

@KtorExperimentalLocationsAPI
@Location(TRANSACTIONS)
class TransactionsRoute

@KtorExperimentalLocationsAPI
fun Route.transactions(transactionRepository: TransactionRepository, userRepository: UserRepository) {
    authenticate("jwt") {
        post<TransactionsRoute> {
            val transaction = call.receive<Transaction>()

            if (transaction.id == null) {
                return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing id"
                )
            }

            if (transaction.userId == null) {
                return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing userId"
                )
            }
            if (transaction.type == null) {
                return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing type"
                )
            }

            val user = call.sessions.get<MySession>()?.let {
                userRepository.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest, "Problems retrieving User"
                )
                return@post
            }

            try {

                val currentTransaction = transactionRepository.addTransaction(transaction)
                currentTransaction?.id?.let {
                    call.respond(HttpStatusCode.OK, currentTransaction)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving transaction")
            }
        }

        get<TransactionsRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val transactions = transactionRepository.getTransactions(user.userId)
                call.respond(transactions)
            } catch (e: Throwable) {
                application.log.error("Failed to get transactions", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting transactions")
            }
        }
    }
}