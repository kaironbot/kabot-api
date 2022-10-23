package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.services.DatabaseService

@Service
class ItemDAO(
    val database: DatabaseService
) {

    fun getAllGuildItems(guildId: String) = database.itemsScope.getAllItems(guildId)

}