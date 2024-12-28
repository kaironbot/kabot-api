package org.wagham.kabotapi.entities.utils

import kotlinx.serialization.Serializable

@Serializable
data class ExpTable(
	val table: Map<Int, String>,
)