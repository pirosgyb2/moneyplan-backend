package com.red.plugins

import com.red.auth.JwtService
import com.red.repository.transactions.TransactionRepository
import com.red.repository.users.UserRepository
import com.red.routes.transactions
import com.red.routes.users
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting(
    userRepository: UserRepository,
    transactionRepository: TransactionRepository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {

    routing {
        users(userRepository, jwtService, hashFunction)
        transactions(transactionRepository, userRepository)
    }

}
