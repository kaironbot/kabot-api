package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class SpellDAO(
    val database: DatabaseComponent
) {

    fun getAllSpells(guildId: String) =
        database.spellsScope.getAllSpells(guildId)

}