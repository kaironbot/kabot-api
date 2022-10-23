package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class FeatDAO(
    val database: DatabaseComponent
) {

    fun getAllFeats(guildId: String) =
        database.featsScope.getAllFeats(guildId)

}