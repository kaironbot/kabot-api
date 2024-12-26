package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.flow.Flow
import org.wagham.db.models.Player
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.logic.PlayerLogic

class PlayerLogicImpl(
	private val database: DatabaseComponent
) : PlayerLogic {

	override fun getPlayers(guildId: String, playerIds: List<String>): Flow<Player> =
		database.playersScope.getPlayers(guildId, playerIds)

}