package org.wagham.kabotapi.entities.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisteredSessionDto(
    val guildId: String,
    val sessionId: String
)