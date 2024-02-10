package org.wagham.kabotapi.entities.dto.items

import kotlinx.serialization.Serializable

@Serializable
data class UpdateInventoryDto(
	val itemId: String,
	val qty: Int,
	val operation: InventoryUpdate
) {
	companion object {

		@Serializable
		enum class InventoryUpdate { BUY, SELL, ASSIGN, TAKE }
	}
}