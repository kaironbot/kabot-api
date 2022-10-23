package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.db.enums.CharacterStatus
import org.wagham.kabotapi.services.DatabaseService

@Service
class CharacterDAO(
    val database: DatabaseService
) {

    suspend fun getActiveCharacter(guildId: String, playerId: String) =
        database.charactersScope.getActiveCharacter(guildId, playerId)

    fun getAllCharacters(guildId: String) =
        database.charactersScope.getAllCharacters(guildId)

    fun getCharactersWithPlayer(guildId: String, status: CharacterStatus? = null) =
        database.charactersScope.getCharactersWithPlayer(guildId, status)

}