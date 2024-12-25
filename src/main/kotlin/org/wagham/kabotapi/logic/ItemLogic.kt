package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Character
import org.wagham.db.models.GenericSession
import org.wagham.db.models.Item
import org.wagham.db.models.Player
import org.wagham.db.models.embed.LabelStub
import org.wagham.kabotapi.entities.PaginatedList
import javax.management.Query

interface ItemLogic {

	/**
	 * Retrieves all [Item]s in a guild.
	 *
	 * @param guildId the id of the guild.
	 * @return a [Flow] of [Item].
	 */
	fun getItems(guildId: String): Flow<Item>

	/**
	 * Retrieves all [Item]s in a guild with a specified id.
	 *
	 * @param guildId the id of the guild.
	 * @param ids a [Set] containing the ids of the items to retrieve.
	 * @return a [Flow] of [Item].
	 */
	fun getItems(guildId: String, ids: Set<String>): Flow<Item>

	/**
	 * Retrieves all the [Item]s for which the item with the specified id is a craft material
	 *
	 * @param guildId the id of the guild.
	 * @param itemId the id of the item.
	 * @return a [Flow] of [Item]s.
	 */
	fun isMaterialOf(guildId: String, itemId: String): Flow<Item>

	/**
	 * Returns all the items in a guild, where [Item.normalizedName] matches the normalized [query] (if present) and that
	 * have the [label] passed as parameter (if present) with support for pagination.
	 *
	 * @param guildId the id of the guild where to retrieve the sessions.
	 * @param label a [LabelStub] that the retrieved items should have. If null, the items for any label will be returned.
	 * @param query a query (prefix) to search by normalized name. If null, the items with any name will be returned.
	 * @param limit the maximum numbers of elements to be included in the page.
	 * @param skip the number of elements to skip to go to the starting element of the page.
	 * @return a [PaginatedList] of [Item].
	 */
	suspend fun searchItems(guildId: String, label: LabelStub? = null, query: String? = null, limit: Int? = null, skip: Int? = null): PaginatedList<Item>

	/**
	 * Returns all the items in a guild, where [Item.normalizedName] matches the normalized [query] (if present) and that
	 * have all the [labels] passed as parameter.
	 *
	 * @param guildId the id of the guild where to retrieve the sessions.
	 * @param labels a [List] of [LabelStub] that the retrieved items should have. If null, the items for any label will be returned.
	 * @param query a query (prefix) to search by normalized name. If null, the items with any name will be returned.
	 * @return a [Flow] of [Item.name]
	 */
	suspend fun listItemIds(guildId: String, labels: List<LabelStub> = emptyList(), query: String? = null): Flow<String>

	/**
	 * Creates an item in a guild.
	 *
	 * @param guildId the id of the guild where to create the item.
	 * @param item the item to create.
	 */
	suspend fun createItem(guildId: String, item: Item)

	/**
	 * Deletes an item in a guild, also removing it from the inventory of all players.
	 *
	 * @param guildId the id of the guild where to delete the item.
	 * @param item the [Item.name] of the item to delete.
	 */
	suspend fun deleteItem(guildId: String, item: String)

	/**
	 * Updates an item ina guild. If the item was renamed, it also renames it in the inventory of all players.
	 *
	 * @param guildId the id of the guild where to delete the item.
	 * @param item the [Item] to update.
	 * @param originalName if the item was renamed, the original name of the item.
	 */
	suspend fun updateItem(guildId: String, item: Item, originalName: String)

	/**
	 * Retrieves all the [Character] that have in [Character.inventory] the specified item.
	 *
	 * @param guildId the id of the guild.
	 * @param itemName the item to search.
	 * @param onlyActive whether to return only active characters.
	 * @return a [Flow] of [Character]s.
	 */
	fun usedBy(guildId: String, itemName: String, onlyActive: Boolean): Flow<Character>

}