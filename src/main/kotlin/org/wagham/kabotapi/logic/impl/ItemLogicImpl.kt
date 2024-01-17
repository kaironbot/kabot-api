package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Item
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.logic.ItemLogic

class ItemLogicImpl(
    private val database: DatabaseComponent
) : ItemLogic {

    override fun getItems(guildId: String): Flow<Item> = database.itemsScope.getAllItems(guildId)

    override fun getItems(guildId: String, ids: Set<String>): Flow<Item> = database.itemsScope.getItems(guildId, ids)

    override fun isMaterialOf(guildId: String, itemId: String): Flow<Item> = database.itemsScope.isMaterialOf(guildId, itemId)

}