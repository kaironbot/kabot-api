package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class UtilityDAO(
    val database: DatabaseComponent
) {

    suspend fun getExpTable(guildId: String) =
        database.utilityScope.getExpTable(guildId)

}