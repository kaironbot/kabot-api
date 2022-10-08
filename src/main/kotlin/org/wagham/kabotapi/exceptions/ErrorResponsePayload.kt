package org.wagham.kabotapi.exceptions

data class ErrorResponsePayload (
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?
)