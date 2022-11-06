package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class RaceDAO(
    val database: DatabaseComponent
) {

    fun getAllRaces(guildId: String) = database.racesScope.getAllRaces(guildId)

}