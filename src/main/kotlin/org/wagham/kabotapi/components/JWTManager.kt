package org.wagham.kabotapi.components

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.wagham.kabotapi.entities.config.JWTConfig
import org.wagham.kabotapi.entities.security.JWTClaims
import org.wagham.kabotapi.entities.security.JWTRefreshClaims
import org.wagham.kabotapi.exceptions.JWTException
import org.wagham.kabotapi.exceptions.UnauthorizedException
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class JWTManager(
    val config: JWTConfig
) {

    companion object {
        const val USER_ID = "uId"
        const val GUILD_ID = "gId"
        const val ROLES = "r"
        const val DISCORD_AUTH_TOKEN = "dJwt"
        const val DISCORD_REFRESH_TOKEN = "rJwt"
        private val authJWTDuration = 60L.minutes.inWholeMilliseconds
        private val refreshJWTDuration = 30L.days.inWholeMilliseconds
    }

    /**
     * Generates an authentication JWT from the claims passed as parameter.
     * The duration of the token is set to 1 hour.
     *
     * @param jwtClaims the [JWTClaims] to put in the token.
     * @return a base64-encoded JWT.
     */
    fun generateAuthJWT(jwtClaims: JWTClaims): String = JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim(USER_ID, jwtClaims.userId)
        .withClaim(GUILD_ID, jwtClaims.guildId)
        .withClaim(DISCORD_AUTH_TOKEN, jwtClaims.discordAuthToken)
        .let {
            if(jwtClaims.roles.isNotEmpty()) it.withClaim(
                ROLES, Json.encodeToString(jwtClaims.roles)
            ) else it
        }
        .withExpiresAt(Date(System.currentTimeMillis() + authJWTDuration))
        .sign(Algorithm.HMAC256(config.authSecret))

    /**
     * @return a [JWTVerifier] for the authentication jwt.
     */
    fun authJWTVerifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(config.authSecret))
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .build()

    private fun Payload.isAuthJwtValid() =
        getClaim(USER_ID).asString().isNotBlank()
            && getClaim(GUILD_ID).asString().isNotBlank()
            && getClaim(DISCORD_AUTH_TOKEN).asString().isNotBlank()

    /**
     * Converts a [JWTCredential] to a [JWTPrincipal], ensuring that the payload is in the correct format.
     *
     * @param credential a [JWTCredential].
     * @return a [JWTPrincipal].
     */
    fun authCredentialToPrincipal(credential: JWTCredential): JWTPrincipal =
        if (credential.payload.isAuthJwtValid()) {
            JWTPrincipal(credential.payload)
        } else throw UnauthorizedException("Wrong JWT format")

    /**
     * Generates a refresh JWT from the claims passed as parameter.
     * The duration of the token is set to 1 hour.
     *
     * @param jwtClaims the [JWTRefreshClaims] to put in the token.
     * @return a base64-encoded JWT.
     */
    fun generateRefreshJWT(jwtClaims: JWTRefreshClaims): String = JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim(USER_ID, jwtClaims.userId)
        .withClaim(GUILD_ID, jwtClaims.guildId)
        .withClaim(DISCORD_REFRESH_TOKEN, jwtClaims.discordRefreshToken)
        .withClaim(USER_ID, jwtClaims.userId)
        .withExpiresAt(Date(System.currentTimeMillis() + refreshJWTDuration))
        .sign(Algorithm.HMAC256(config.refreshSecret))

    /**
     * @return a [JWTVerifier] for the refresh jwt.
     */
    fun refreshJWTVerifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(config.refreshSecret))
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .build()

    private fun Payload.isRefreshJwtValid() =
        getClaim(USER_ID).asString().isNotBlank()
            && getClaim(GUILD_ID).asString().isNotBlank()
            && getClaim(DISCORD_REFRESH_TOKEN).asString().isNotBlank()

    /**
     * Converts a [JWTCredential] to a [JWTPrincipal], ensuring that the payload is in the correct format.
     *
     * @param credential a [JWTCredential].
     * @return a [JWTPrincipal].
     */
    fun refreshCredentialToPrincipal(credential: JWTCredential): JWTPrincipal =
        if (credential.payload.isRefreshJwtValid()) {
            JWTPrincipal(credential.payload)
        } else throw UnauthorizedException("Wrong JWT format")
}

/**
 * Converts a [Payload] to [JWTClaims].
 *
 * @receiver payload a [Payload].
 * @return a [JWTClaims]
 * @throws JWTException if it the JWT is in the wrong format.
 */
fun Payload.toJWTClaims(): JWTClaims = try {
    JWTClaims(
        userId = getClaim(JWTManager.USER_ID).asString(),
        guildId = getClaim(JWTManager.GUILD_ID).asString(),
        discordAuthToken = getClaim(JWTManager.DISCORD_AUTH_TOKEN).asString(),
        roles = claims[JWTManager.ROLES]?.let { Json.decodeFromString(it.asString()) } ?: emptySet()
    )
} catch(e: Exception) {
    throw JWTException(e.message ?: "Wrong JWT format")
}

/**
 * Converts a [Payload] to [JWTRefreshClaims].
 *
 * @receiver payload a [Payload].
 * @return a [JWTRefreshClaims]
 * @throws JWTException if the JWT is in the wrong format.
 */
fun Payload.toJWTRefreshClaims(): JWTRefreshClaims = try {
    JWTRefreshClaims(
        userId = getClaim(JWTManager.USER_ID).asString(),
        guildId = getClaim(JWTManager.GUILD_ID).asString(),
        discordRefreshToken = getClaim(JWTManager.DISCORD_REFRESH_TOKEN).asString()
    )
} catch(e: Exception) {
    throw JWTException(e.message ?: "Wrong JWT format")
}