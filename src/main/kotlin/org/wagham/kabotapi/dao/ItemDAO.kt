package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class ItemDAO(
    val database: DatabaseComponent
) {

    fun getAllGuildItems(guildId: String) = database.itemsScope.getAllItems(guildId)

}