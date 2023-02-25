package org.wagham.kabotapi.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReleaseAsset(
    val url: String,
    val name: String
)