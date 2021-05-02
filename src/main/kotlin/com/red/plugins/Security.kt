package com.red.plugins

import io.ktor.application.*
import io.ktor.auth.*

fun Application.configureSecurity() {
    install(Authentication) {
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
