package org.wagham.kabotapi.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.util.*

@Component
class JwtUtils{

    companion object {
        private val expirationTimeMillis: Long = Duration.ofHours(1).toMillis()
        private val refreshExpirationTimeMillis: Long = Duration.ofDays(365).toMillis()

        private const val USER_ID = "uId"
        private const val GUILD_ID = "gId"
        private const val AUTHORITIES = "a"

        private const val PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----"
        private const val PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----"
        private const val PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----"
        private const val PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----"

        fun createKeyPairFromString(publicKey: String, privateKey: String): KeyPair {
            return KeyPair(
                decodePublicKeyFromString(publicKey),
                decodePrivateKeyFromString(privateKey)
            )
        }

        private fun decodePublicKeyFromString(publicKey: String): PublicKey {
            val publicBytes = Base64.getDecoder().decode(
                publicKey
                    .replace("\n", "")
                    .replace(PUBLIC_KEY_HEADER, "")
                    .replace(PUBLIC_KEY_FOOTER, "")
            )
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(X509EncodedKeySpec(publicBytes))
        }

        private fun decodePrivateKeyFromString(privateKey: String): PrivateKey {
            val privateBytes = Base64.getDecoder().decode(
                privateKey
                    .replace("\n", "")
                    .replace(PRIVATE_KEY_HEADER, "")
                    .replace(PRIVATE_KEY_FOOTER, "")
            )
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateBytes))
        }

    }

    private val authKeyPair: KeyPair
    private val refreshKeyPair: KeyPair

    init {
        if (System.getenv("JWT_AUTH_PUB_KEY") == null
            || System.getenv("JWT_AUTH_PRIV_KEY") == null
            || System.getenv("JWT_REFRESH_PUB_KEY") == null
            || System.getenv("JWT_REFRESH_PRIV_KEY") == null) {
            authKeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)
            refreshKeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)
        }
        else {
            authKeyPair = createKeyPairFromString(
                System.getenv("JWT_AUTH_PUB_KEY"),
                System.getenv("JWT_AUTH_PRIV_KEY")
            )
            refreshKeyPair = createKeyPairFromString(
                System.getenv("JWT_REFRESH_PUB_KEY"),
                System.getenv("JWT_REFRESH_PRIV_KEY")
            )
        }
    }

    fun createJWT(details: JwtDetails): String {
        return Jwts.builder()
            .setClaims(
                mapOf(
                    USER_ID to details.userId,
                    GUILD_ID to details.guildId,
                    AUTHORITIES to details.authorities.map { it.authority }
                )
            )
            .setExpiration(Date(System.currentTimeMillis() + expirationTimeMillis))
            .signWith(authKeyPair.private, SignatureAlgorithm.RS256)
            .compact()
    }

    fun jwtDetailsFromClaims(
        it: Claims
    ) = JwtDetails(
        userId = it[USER_ID] as String,
        guildId = it[GUILD_ID] as String,
        authorities = (it[AUTHORITIES] as Collection<Any?>)
            .filterIsInstance<String>()
            .fold(setOf<GrantedAuthority>()) { acc, x -> acc + SimpleGrantedAuthority(x) }.toMutableSet()
    )

    /**
     * Decodes a JWT and gets the Claims. Throws an exception if the token is not valid or expired, unless the
     * ignoreExpired parameter is set to true. In this case, the claims of the expired token will be user, but are not
     * to be trusted.
     * @param jwt the encoded JWT.
     * @param ignoreExpiration whether to return the Claims even if the token is expired.
     * @return the claims.
     */
    fun decodeAndGetClaims(jwt: String, ignoreExpiration: Boolean = false): Claims =
        try {
            Jwts.parserBuilder()
                .setSigningKey(authKeyPair.public)
                .build()
                .parseClaimsJws(jwt)
                .body
        } catch (e: ExpiredJwtException) {
            if(ignoreExpiration) {
                e.claims
            } else throw e
        }

    fun createRefreshJWT(details: JwtDetails): String {
        return Jwts.builder()
            .setClaims(
                mapOf(
                    "userId" to details.userId,
                    "guildId" to details.guildId
                )
            )
            .setExpiration(Date(System.currentTimeMillis() + refreshExpirationTimeMillis))
            .signWith(refreshKeyPair.private, SignatureAlgorithm.RS256)
            .compact()
    }

    fun decodeAndGetRefreshClaims(jwt: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(refreshKeyPair.public)
            .build()
            .parseClaimsJws(jwt)
            .body
    }

}