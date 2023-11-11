package org.wagham.kabotapi.entities.config

import io.ktor.server.config.*

class JWTConfig(
    val authSecret: String,
    val refreshSecret: String,
    val issuer: String,
    val audience: String,
    val realm: String
) {
    companion object {
        fun fromConfig(config: ApplicationConfig) = JWTConfig(
            authSecret = config.property("ktor.jwt.authSecret").getString(),
            refreshSecret = config.property("ktor.jwt.refreshSecret").getString(),
            issuer = config.property("ktor.jwt.issuer").getString(),
            audience = config.property("ktor.jwt.audience").getString(),
            realm = config.property("ktor.jwt.realm").getString()
        )
    }
}