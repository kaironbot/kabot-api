package org.wagham.kabotapi.entities

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String, val code: Int)