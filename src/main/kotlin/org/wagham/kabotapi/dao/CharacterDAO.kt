package org.wagham.kabotapi.dao

import kotlinx.coroutines.flow.Flow
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

    /**
     * @param guildId the guild id. Is the unique identifier for a Discord server.
     * @param playerId the player id. Is the unique identifier for a Discord user.
     * @return a Flow containing all the active characters for the player in the guild.
     */
    fun getActiveCharacters(guildId: String, playerId: String) =
        database.charactersScope.getActiveCharacters(guildId, playerId)

}