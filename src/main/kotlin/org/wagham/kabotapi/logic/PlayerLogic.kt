package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Player

interface PlayerLogic {

	/**
	 * Retrieves a set of players in a guild by their ids.
	 *
	 * @param guildId the id of the guild.
	 * @param playerIds a list of [Player.playerId].
	 * @return a [Flow] of [Player].
	 */
	fun getPlayers(guildId: String, playerIds: List<String>): Flow<Player>

}