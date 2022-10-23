package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class BackgroundDAO(
    val database: DatabaseComponent
) {

    fun getAllGuildBackgrounds(guildId: String) = database.backgroundsScope.getAllBackgrounds(guildId)

}