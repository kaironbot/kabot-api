package org.wagham.kabotapi.logic.impl

import org.wagham.kabotapi.dao.CharacterDao
import org.wagham.kabotapi.logic.CharacterLogic

class CharacterLogicImpl(
    private val characterDao: CharacterDao
): CharacterLogic {
    override fun getActiveCharacters(guildId: String, playerId: String) =
        characterDao.getActiveCharacters(guildId, playerId)
}