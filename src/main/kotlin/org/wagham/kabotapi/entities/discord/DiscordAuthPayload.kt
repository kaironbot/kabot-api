package org.wagham.kabotapi.entities.discord

import com.fasterxml.jackson.annotation.JsonProperty

data class DiscordAuthPayload(
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("client_secret") val clientSecret: String,
    @JsonProperty("grant_type") val grantType: String,
    val code: String,
    @JsonProperty("redirect_url") val redirectUrl: String
)