package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.services.DatabaseService

@Service
class BackgroundDAO(
    val database: DatabaseService
) {

    fun getAllGuildBackgrounds(guildId: String) = database.backgroundsScope.getAllBackgrounds(guildId)

}