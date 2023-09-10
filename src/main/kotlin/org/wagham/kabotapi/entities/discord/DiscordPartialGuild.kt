package org.wagham.kabotapi.entities.discord

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordPartialGuild(
    val id: String,
    val name: String,
    val icon: String?,
    val owner: Boolean,
    val permissions: String,
    val features: List<String>,
    @JsonProperty("approximate_member_count") val approxMemberCount: Int,
    @JsonProperty("approximate_presence_count") val approxPresenceCount: Int

)