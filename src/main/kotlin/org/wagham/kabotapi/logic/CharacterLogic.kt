package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Character
import org.wagham.db.pipelines.characters.CharacterWithPlayer

interface CharacterLogic {

    /**
     * Retrieves all the active characters for a player ina guild.
     *
     * @param guildId the guild id. It is the unique identifier for a Discord server.
     * @param playerId the player id. It is the unique identifier for a Discord user.
     * @return a [Flow] containing all the active [Character]s for the player in the guild.
     */
    fun getActiveCharacters(guildId: String, playerId: String): Flow<Character>

    /**
     * Returns all the active characters in a guild.
     *
     * @param guildId the guild id. It is the unique identifier for a Discord server.
     * @return a [Flow] containing all the active [Character]s in the guild.
     */
    fun getAllActiveCharacters(guildId: String): Flow<Character>

    /**
     * Returns all the active characters in a guild, embedding the information about the player.
     *
     * @param guildId the guild id. It is the unique identifier for a Discord server.
     * @return a [Flow] containing all the active [Character]s in the guild.
     */
    fun getAllActiveCharactersWithPlayer(guildId: String): Flow<CharacterWithPlayer>

}