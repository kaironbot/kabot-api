package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.flow.Flow
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.models.Character
import org.wagham.db.pipelines.characters.CharacterWithPlayer
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.logic.CharacterLogic

class CharacterLogicImpl(
    private val database: DatabaseComponent
): CharacterLogic {
    override fun getActiveCharacters(guildId: String, playerId: String) =
        database.charactersScope.getActiveCharacters(guildId, playerId)

    override fun getAllActiveCharacters(guildId: String): Flow<Character> =
        database.charactersScope.getAllCharacters(guildId, CharacterStatus.active)

    override fun getAllActiveCharactersWithPlayer(guildId: String): Flow<CharacterWithPlayer> =
        database.charactersScope.getCharactersWithPlayer(guildId, CharacterStatus.active)
}