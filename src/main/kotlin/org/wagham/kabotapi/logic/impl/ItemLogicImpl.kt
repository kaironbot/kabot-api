package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.wagham.db.models.Character
import org.wagham.db.models.Item
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.utils.StringNormalizer
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.entities.PaginatedList
import org.wagham.kabotapi.logic.ItemLogic

class ItemLogicImpl(
	private val database: DatabaseComponent
) : ItemLogic {

	override fun getItems(guildId: String): Flow<Item> = database.itemsScope.getAllItems(guildId)

	override fun getItems(guildId: String, ids: Set<String>): Flow<Item> = database.itemsScope.getItems(guildId, ids)

	override fun isMaterialOf(guildId: String, itemId: String): Flow<Item> = database.itemsScope.isMaterialOf(guildId, itemId)

	override suspend fun searchItems(
		guildId: String,
		label: LabelStub?,
		query: String?,
		limit: Int?,
		skip: Int?
	): PaginatedList<Item> {
		val items = database.itemsScope.getItemsMatching(
			guildId = guildId,
			labels = listOfNotNull(label),
			query = query,
			limit = limit,
			skip = skip
		).toList()
		return PaginatedList(
			entities = items,
			nextAt = if(items.size == limit) limit + (skip ?: 0) else null
		)
	}

	override suspend fun listItemIds(guildId: String, labels: List<LabelStub>, query: String?): Flow<String> =
		database.itemsScope.getItemsMatching(
			guildId = guildId,
			labels = labels,
			query = query
		).map { it.name }

	override suspend fun createItem(guildId: String, item: Item) {
		val result = database.itemsScope.createOrUpdateItem(
			guildId,
			item.copy(
				normalizedName = StringNormalizer.normalize(item.name)
			)
		)
		if (!result.committed) {
			throw IllegalStateException("Item creation failed")
		}
	}

	override suspend fun deleteItem(guildId: String, item: String) {
		database.transaction(guildId) {
			database.itemsScope.deleteItems(it, guildId, listOf(item))
			database.charactersScope.removeItemFromAllInventories(it, guildId, item)
		}.also { result ->
			if (!result.committed) {
				throw IllegalStateException("Item deletion failed")
			}
		}
	}

	override suspend fun updateItem(guildId: String, item: Item, originalName: String) {
		database.transaction(guildId) {
			if (originalName == item.name) {
				database.itemsScope.createOrUpdateItem(it, guildId, item)
			} else {
				database.itemsScope.deleteItems(it, guildId, listOf(originalName))
				database.itemsScope.createOrUpdateItem(
					it, guildId, item.copy(
						normalizedName = StringNormalizer.normalize(item.name)
					)
				)
				database.charactersScope.renameItemInAllInventories(it, guildId, originalName, item.name)
			}
		}.also { result ->
			if (!result.committed) {
				throw IllegalStateException("Item deletion failed")
			}
		}
	}

	override fun usedBy(guildId: String, itemName: String, onlyActive: Boolean): Flow<Character> =
		database.itemsScope.getCharactersWithItem(guildId, itemName, onlyActive)
}