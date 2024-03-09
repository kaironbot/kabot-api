package org.wagham.kabotapi.entities

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedList<T>(
	val entities: List<T>,
	val nextAt: Int? = null
)
