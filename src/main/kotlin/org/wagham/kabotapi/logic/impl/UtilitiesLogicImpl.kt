package org.wagham.kabotapi.logic.impl

import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.entities.utils.ExpTable
import org.wagham.kabotapi.logic.UtilitiesLogic

class UtilitiesLogicImpl(
	private val database: DatabaseComponent
): UtilitiesLogic {

	override suspend fun getExpTable(guildId: String): ExpTable =
		database.utilityScope.getExpTable(guildId).let {
			ExpTable(it.table)
		}


}