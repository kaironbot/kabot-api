package org.wagham.kabotapi.entities.dto

import kotlinx.serialization.Serializable

@Serializable
data class LevelUpDto(
    val guildId: String,
    val updates: Map<String, Float>
)