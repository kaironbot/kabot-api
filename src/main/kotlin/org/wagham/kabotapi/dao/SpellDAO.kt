package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.services.DatabaseService

@Service
class SpellDAO(
    val database: DatabaseService
) {

    fun getAllSpells(guildId: String) =
        database.spellsScope.getAllSpells(guildId)

}