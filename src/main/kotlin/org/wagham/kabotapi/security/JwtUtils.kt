package org.wagham.kabotapi.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Component
import org.wagham.kabotapi.exceptions.UnauthorizedException
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class JwtUtils(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
){

    companion object {
        private const val USER_ID = "uId"
        private const val GUILD_ID = "gId"
        private const val AUTHORITIES = "a"
        private const val DISCORD_AUTH_TOKEN = "dJwt"
        private const val DISCORD_REFRESH_TOKEN = "rJwt"
    }

    fun createJWT(details: JwtDetails, expirationTime: Long): String {
        val header = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(expirationTime))
            .claim(USER_ID, details.userId)
            .claim(GUILD_ID, details.guildId)
            .claim(AUTHORITIES, details.authorities.map { it.authority })
            .claim(DISCORD_AUTH_TOKEN, details.discordAuthToken)
            .build()
        return jwtEncoder
            .encode(JwtEncoderParameters.from(header, claims))
            .tokenValue
    }

    fun decodeAndGetDetails(encodedJwt: String): JwtDetails =
        jwtDecoder.decode(encodedJwt).let { jwt ->
            JwtDetails(
                userId = jwt.claims[USER_ID] as String,
                guildId = jwt.claims[GUILD_ID] as String,
                discordAuthToken = jwt.claims[DISCORD_AUTH_TOKEN] as String,
                authorities = (jwt.claims[AUTHORITIES] as Collection<Any?>)
                    .filterIsInstance<String>()
                    .fold(setOf<GrantedAuthority>()) { acc, x -> acc + SimpleGrantedAuthority(x) }.toMutableSet()
            )
        }


    fun createRefreshJWT(details: JwtDetails): String {
        val header = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
            .claim(USER_ID, details.userId)
            .claim(GUILD_ID, details.guildId)
            .claim(DISCORD_REFRESH_TOKEN, details.discordRefreshToken)
            .build()
        return jwtEncoder
            .encode(JwtEncoderParameters.from(header, claims))
            .tokenValue
    }

    fun extractRefreshDetailsFromToken(encodedJwt: String) = encodedJwt.replace("Bearer ", "")
        .let { rawToken ->
            jwtDecoder.decode(rawToken).claims.let { claims ->
                JwtDetails(
                    emptySet(),
                    (claims[USER_ID] as? String) ?: throw UnauthorizedException("Cannot refresh token, missing user id"),
                    (claims[GUILD_ID] as? String) ?: throw UnauthorizedException("Cannot refresh token, missing guild id"),
                    "",
                    (claims[DISCORD_REFRESH_TOKEN] as? String) ?: throw UnauthorizedException("Cannot refresh token, missing discord refresh token")
                )
            }
        }

}