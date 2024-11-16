package org.wagham.kabotapi.entities.dto.items

import kotlinx.serialization.Serializable
import org.wagham.db.models.Item

@Serializable
data class ItemUpdate(
	val originalName: String,
	val item: Item
)