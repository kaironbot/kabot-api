package org.wagham.kabotapi.components

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.MongoCredentials
import org.wagham.kabotapi.entities.config.MongoConfig

class DatabaseComponent(
    config: MongoConfig
) {
    private val database = KabotMultiDBClient(
        MongoCredentials(
            "ADMIN",
            config.adminUser,
            config.adminPwd,
            config.databaseName,
            config.databaseIp,
            config.databasePort
        )
    )

    val registeredGuilds
        get() = database.getAllGuildsId()

    val backgroundsScope
        get() = database.backgroundsScope
    val buildingsScope
        get() = database.buildingsScope
    val charactersScope
        get() = database.charactersScope
    val featsScope
        get() = database.featsScope
    val itemsScope
        get() = database.itemsScope
    val playersScope
        get() = database.playersScope
    val racesScope
        get() = database.raceScope
    val spellsScope
        get() = database.spellsScope
    val subclassesScope
        get() = database.subclassesScope
    val utilityScope
        get() = database.utilityScope

}