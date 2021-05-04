package com.red.plugins

import com.red.auth.JwtService
import com.red.repository.UserRepository
import com.red.routes.users
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

fun Application.configureRouting(
    userRepository: UserRepository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    install(Locations) {}


    routing {
        users(userRepository, jwtService, hashFunction)
    }
}
