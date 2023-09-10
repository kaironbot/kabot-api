package org.wagham.kabotapi.entities

data class JwtResponse(
    val authToken: String,
    val refreshToken: String
)