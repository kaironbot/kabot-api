package org.wagham.kabotapi.entities

data class GuildMember(
    val id: String,
    val username: String,
    val avatar: String,
    val roles: Set<String>,
    val pending: Boolean
)
