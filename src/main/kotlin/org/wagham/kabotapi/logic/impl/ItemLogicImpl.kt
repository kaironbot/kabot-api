package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.wagham.db.models.Item
import org.wagham.db.models.embed.LabelStub
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

}