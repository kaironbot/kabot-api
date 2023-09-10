package org.wagham.kabotapi.entities.discord

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordGuildUser(
    val user: DiscordGlobalUser?,
    val nick: String?,
    val avatar: String?,
    val roles: Set<String>?,
    val pending: Boolean?
)
