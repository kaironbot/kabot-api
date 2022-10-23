package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.services.DatabaseService

@Service
class FeatDAO(
    val database: DatabaseService
) {

    fun getAllFeats(guildId: String) =
        database.featsScope.getAllFeats(guildId)

}