package org.wagham.kabotapi.dao

import org.springframework.stereotype.Service
import org.wagham.kabotapi.services.DatabaseService

@Service
class SubclassDAO(
    val database: DatabaseService
) {

    fun getAllSubclasses(guildId: String) =
        database.subclassesScope.getAllSubclasses(guildId)

}