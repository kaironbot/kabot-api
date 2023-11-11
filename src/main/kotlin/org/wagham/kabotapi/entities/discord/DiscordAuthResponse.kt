package org.wagham.kabotapi.entities.discord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordAuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiration: Long,
    @SerialName("refresh_token") val refreshToken: String,
    val scope: String
)