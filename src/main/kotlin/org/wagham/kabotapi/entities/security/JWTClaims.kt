package org.wagham.kabotapi.entities.security

import org.wagham.db.enums.NyxRoles

data class JWTClaims(
    val userId: String,
    val guildId: String,
    val discordAuthToken: String,
    val roles: Set<NyxRoles>
)