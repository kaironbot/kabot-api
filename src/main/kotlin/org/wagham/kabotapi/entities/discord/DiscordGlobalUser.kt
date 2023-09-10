package org.wagham.kabotapi.entities.discord

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordGlobalUser(
    val id: String,
    val username: String,
    val discriminator: String,
    @JsonProperty("global_name") val globalName: String?,
    val avatar: String
)