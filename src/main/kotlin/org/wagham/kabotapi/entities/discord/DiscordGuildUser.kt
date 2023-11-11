package org.wagham.kabotapi.entities.discord

import kotlinx.serialization.Serializable

@Serializable
data class DiscordGuildUser(
    val user: DiscordGlobalUser?,
    val nick: String?,
    val avatar: String?,
    val roles: Set<String>?,
    val pending: Boolean?
)
