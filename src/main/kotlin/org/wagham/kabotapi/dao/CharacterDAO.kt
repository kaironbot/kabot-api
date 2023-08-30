package org.wagham.kabotapi.dao

import kotlinx.coroutines.flow.first
import org.springframework.stereotype.Service
import org.wagham.db.enums.CharacterStatus
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class CharacterDAO(
    val database: DatabaseComponent
) {

    suspend fun getActiveCharacter(guildId: String, playerId: String) =
        database.charactersScope.getActiveCharacters(guildId, playerId).first()

    fun getAllCharacters(guildId: String) =
        database.charactersScope.getAllCharacters(guildId)

    fun getCharactersWithPlayer(guildId: String, status: CharacterStatus? = null) =
        database.charactersScope.getCharactersWithPlayer(guildId, status)

}