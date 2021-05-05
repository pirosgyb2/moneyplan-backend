package com.red.plugins

import com.red.auth.JwtService
import com.red.repository.users.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureSecurity(userRepository: UserRepository, jwtService: JwtService) {
    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Moneyplan Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = userRepository.findUser(claimString)
                user
            }
        }
    }
}
//    val jwtIssuer = environment.config.property("jwt.domain").getString()
//    val jwtAudience = environment.config.property("jwt.audience").getString()
//    val jwtRealm = environment.config.property("jwt.realm").getString()
//    authentication {
//        jwt {
//            realm = jwtRealm
//            verifier(makeJwtVerifier(jwtIssuer, jwtAudience))
//            validate { credential ->
//                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
//            }
//        }
//    }
//
//}
//
//private val algorithm = Algorithm.HMAC256("secret")
//private fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier = JWT
//    .require(algorithm)
//    .withAudience(audience)
//    .withIssuer(issuer)
//    .build()
