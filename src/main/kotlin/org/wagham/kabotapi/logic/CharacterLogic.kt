package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Character
import org.wagham.db.models.CharacterSheet
import org.wagham.db.models.Errata
import org.wagham.db.models.embed.CharacterToken
import org.wagham.db.pipelines.characters.CharacterWithPlayer
import org.wagham.kabotapi.entities.StatusResponse
import org.wagham.kabotapi.entities.dto.items.UpdateInventoryDto
import org.wagham.kabotapi.utils.MimeType

interface CharacterLogic {

	/**
	 * Retrieves all the active [Character]s for a player in a guild.
	 *
	 * @param guildId the guild id. It is the unique identifier for a Discord server.
	 * @param playerId the player id. It is the unique identifier for a Discord user.
	 * @return a [Flow] containing all the active [Character]s for the player in the guild.
	 */
	fun getActiveCharacters(guildId: String, playerId: String): Flow<Character>

	/**
	 * Retrieves a [Character] for a player.
	 *
	 * @param guildId the guild id. It is the unique identifier for a Discord server.
	 * @param playerId the player id. It is the unique identifier for a Discord user.
	 * @param characterId the character id.
	 * @return the retrieved [Character]
	 * @throws IllegalAccessException if the [Character.player] is different from [playerId].
	 */
	suspend fun getCharacter(guildId: String, playerId: String, characterId: String): Character

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

	/**
	 * Updates the inventory of a character.
	 *
	 * @param guildId the id of the guild.
	 * @param playerId the id of the player.
	 * @param characterId the id of the character.
	 * @param updateData a [UpdateInventoryDto] containing the necessary information to update the inventory.
	 * @throws IllegalAccessException if the [Character.player] is different from [playerId].
	 * @throws IllegalArgumentException if the character does not have the requirement to complete the operation.
	 * @throws org.wagham.kabotapi.exceptions.NotFoundException If the specified item does not exist.
	 */
	suspend fun updateInventory(guildId: String, playerId: String, characterId: String, updateData: UpdateInventoryDto)

	/**
	 * Updates the character token in their sheet.
	 *
	 * @param guildId the id of the guild.
	 * @param playerId the id of the player.
	 * @param characterId the id of the character.
	 * @param token the character token image to upload.
	 * @param mimeType the [MimeType] of the image.
	 * @return true if the operation succeeds, false otherwise.
	 * @throws IllegalAccessException if the [Character.player] is different from [playerId].
	 */
	suspend fun setCharacterToken(guildId: String, playerId: String, characterId: String, token: ByteArray, mimeType: MimeType): Boolean

	/**
	 * Retrieves the character token in their sheet.
	 *
	 * @param guildId the id of the guild.
	 * @param characterId the id of the character.
	 * @return the [CharacterToken]
	 * @throws org.wagham.kabotapi.exceptions.NotFoundException If the specified character token does not exist.
	 */
	suspend fun getCharacterToken(guildId: String, characterId: String): CharacterToken

	/**
	 * Retrieves the [CharacterSheet] for a character in a guild.
	 *
	 * @param guildId the id of the guild.
	 * @param characterId the id of the character.
	 * @return a [CharacterSheet]
	 * @throws org.wagham.kabotapi.exceptions.NotFoundException If the specified character sheet does not exist.
	 */
	suspend fun getCharacterSheet(guildId: String, characterId: String): CharacterSheet

	/**
	 * Retrieves all the [Character]s for the player with the specified [playerId].
	 *
	 * @param guildId the id of the guild.
	 * @param playerId the id of the player.
	 * @return a [Flow] of [Character].
	 */
	fun getCharactersForPlayer(guildId: String, playerId: String): Flow<Character>
}