package org.wagham.kabotapi.entities.security

data class JWTClaims(
    val userId: String,
    val guildId: String,
    val discordAuthToken: String,
)