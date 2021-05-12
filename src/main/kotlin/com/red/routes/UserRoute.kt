package com.red.routes

import com.red.API_VERSION
import com.red.auth.JwtService
import com.red.auth.MySession
import com.red.models.containers.LoginBody
import com.red.models.containers.RegistrationBody
import com.red.models.containers.SessionResponse
import com.red.models.containers.UserResponse
import com.red.repository.users.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_LOGOUT = "$USERS/logout"
const val USER_ME = "$USERS/me"


@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute


@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
@Location(USER_LOGOUT)
class UserLogoutRoute

@KtorExperimentalLocationsAPI
@Location(USER_ME)
class UserMeRoute


@KtorExperimentalLocationsAPI
fun Route.users(
    repository: UserRepository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserCreateRoute> {
        val registrationBody = call.receive<RegistrationBody>()
        val password = registrationBody.password
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val displayName = registrationBody.displayName
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val email = registrationBody.email
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val hash = hashFunction(password)
        try {
            val newUser = repository.addUser(email, displayName, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                val response = SessionResponse(
                    token = jwtService.generateToken(newUser),
                    userId = newUser.userId
                )
                call.respond(HttpStatusCode.Created, response)
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    post<UserLoginRoute> {
        val loginBody = call.receive<LoginBody>()
        val password = loginBody.password
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val email = loginBody.email
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val hash = hashFunction(password)
        try {
            val currentUser = repository.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(MySession(it))
                    val response = SessionResponse(
                        token = jwtService.generateToken(currentUser),
                        userId = currentUser.userId
                    )
                    call.respond(response)
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest, "Problems retrieving User"
                    )
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }

    post<UserLogoutRoute> {
        try {
            call.sessions.clear<MySession>()
            call.respond(HttpStatusCode.OK)
        } catch (e: Throwable) {
            application.log.error("Failed to logout user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems logout User")
        }
    }

    get<UserMeRoute> {
        val userId = call.getUserId(repository) ?: return@get

        try {
            val user = repository.findUser(userId)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "There is no user with that id")
            } else {
                call.respond(UserResponse(userName = user.displayName, userEmail = user.email))
            }
        } catch (e: Throwable) {
            application.log.error("Failed to get transactions", e)
            call.respond(HttpStatusCode.BadRequest, "Problems getting transactions")
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