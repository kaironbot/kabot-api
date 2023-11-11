package org.wagham.kabotapi.dao.impl

import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.dao.CharacterDao

class CharacterDaoImpl(
    private val database: DatabaseComponent
) : CharacterDao {

    override fun getActiveCharacters(guildId: String, playerId: String) =
        database.charactersScope.getActiveCharacters(guildId, playerId)

}