package com.red

import com.red.auth.JwtService
import com.red.auth.hash
import com.red.plugins.configureRouting
import com.red.plugins.configureSecurity
import com.red.plugins.configureSerialization
import com.red.plugins.configureSessions
import com.red.repository.DatabaseFactory
import com.red.repository.UserRepository
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        DatabaseFactory.init()
        val userRepository = UserRepository()
        val jwtService = JwtService()
        val hashFunction = { s: String -> hash(s) }

        configureRouting(userRepository, jwtService, hashFunction)
        configureSecurity(userRepository, jwtService)
        configureSessions()
        configureSerialization()
    }.start(wait = true)
}

const val API_VERSION = "/v1"