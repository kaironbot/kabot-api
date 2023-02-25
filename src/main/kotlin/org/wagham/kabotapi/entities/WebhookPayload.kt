package org.wagham.kabotapi.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookPayload(
    val action: String,
    val release: Release
)