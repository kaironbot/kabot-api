package org.wagham.kabotapi.entities.discord

import kotlinx.serialization.Serializable

@Serializable
data class DiscordAuthCode(val code: String)