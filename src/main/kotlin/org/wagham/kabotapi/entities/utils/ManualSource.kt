package org.wagham.kabotapi.entities.utils

import kotlinx.serialization.Serializable

@Serializable
data class ManualSource(
	val name: String,
	val abbreviation: String
)