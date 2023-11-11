package org.wagham.kabotapi.dao

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Character

interface CharacterDao {

    /**
     * Retrieves all the active characters for a player ina guild.
     *
     * @param guildId the guild id. Is the unique identifier for a Discord server.
     * @param playerId the player id. Is the unique identifier for a Discord user.
     * @return a [Flow] containing all the active [Character]s for the player in the guild.
     */
    fun getActiveCharacters(guildId: String, playerId: String): Flow<Character>
}