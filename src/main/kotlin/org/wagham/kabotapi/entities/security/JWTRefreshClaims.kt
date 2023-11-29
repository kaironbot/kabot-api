package org.wagham.kabotapi.entities.security

data class JWTRefreshClaims(
    val userId: String,
    val guildId: String,
    val discordRefreshToken: String
)