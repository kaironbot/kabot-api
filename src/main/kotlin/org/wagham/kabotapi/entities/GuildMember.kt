package org.wagham.kabotapi.entities

import kotlinx.serialization.Serializable

@Serializable
data class GuildMember(
    val id: String,
    val username: String,
    val avatar: String?,
    val roles: Set<String>,
    val pending: Boolean
)
