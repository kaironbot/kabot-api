package org.wagham.kabotapi.entities.security

import kotlinx.serialization.Serializable

@Serializable
data class JwtResponse(
    val authToken: String,
    val refreshToken: String
)