package org.wagham.kabotapi.entities.discord

import kotlinx.serialization.Serializable

@Serializable
data class DiscordPartialGuild(
    val id: String,
    val name: String,
    val icon: String?,
    val owner: Boolean,
    val permissions: String,
    val features: List<String>
)