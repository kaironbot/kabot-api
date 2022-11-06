package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class BuildingDAO(
    val database: DatabaseComponent
) {

    fun getAllBuildings(guildId: String) = database.buildingsScope.getBuildingsWithBounty(guildId)

}