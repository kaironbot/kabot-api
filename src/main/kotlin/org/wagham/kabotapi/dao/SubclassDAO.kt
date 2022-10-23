package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.components.DatabaseComponent

@Service
class SubclassDAO(
    val database: DatabaseComponent
) {

    fun getAllSubclasses(guildId: String) =
        database.subclassesScope.getAllSubclasses(guildId)

}