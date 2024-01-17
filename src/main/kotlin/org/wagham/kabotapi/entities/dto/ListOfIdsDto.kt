package org.wagham.kabotapi.entities.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListOfIdsDto(
    val ids: Set<String>
)
