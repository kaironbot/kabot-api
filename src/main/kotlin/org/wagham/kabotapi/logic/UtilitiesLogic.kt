package org.wagham.kabotapi.logic

import org.wagham.kabotapi.entities.utils.ExpTable

interface UtilitiesLogic {

	/**
	 * Retrieves the [ExpTable] for the provided guild.
	 * @param guildId the id of the guild.
	 * @return an [ExpTable].
	 */
	suspend fun getExpTable(guildId: String): ExpTable

}