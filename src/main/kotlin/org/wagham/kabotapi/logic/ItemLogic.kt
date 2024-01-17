package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Item

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

}