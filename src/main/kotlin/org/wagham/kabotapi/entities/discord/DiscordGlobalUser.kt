package org.wagham.kabotapi.entities.discord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordGlobalUser(
    val id: String,
    val username: String,
    val discriminator: String,
    @SerialName("global_name") val globalName: String?,
    val avatar: String?
)