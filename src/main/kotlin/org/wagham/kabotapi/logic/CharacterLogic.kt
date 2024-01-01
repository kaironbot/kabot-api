package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Character
import org.wagham.db.models.Errata
import org.wagham.db.pipelines.characters.CharacterWithPlayer
import org.wagham.kabotapi.entities.StatusResponse

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

    /**
     * Adds an [Errata] to a character, identified by its id, in a guild.
     *
     * @param guildId the id of the guild of the character.
     * @param characterId the id of the character to update.
     * @param errata the [Errata] to add.
     * @return a [StatusResponse].
     */
    suspend fun addErrata(guildId: String, characterId: String, errata: Errata): StatusResponse

}