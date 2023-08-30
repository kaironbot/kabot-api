package org.wagham.kabotapi.entities.discord

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscordAuthResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expires_in") val expiration: Long,
    @JsonProperty("refresh_token") val refreshToken: String,
    val scope: String
)