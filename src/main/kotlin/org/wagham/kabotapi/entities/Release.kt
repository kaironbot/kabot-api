package org.wagham.kabotapi.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Release(
    @JsonProperty("tag_name") val version: String,
    val url: String,
    val assets: List<ReleaseAsset>
)