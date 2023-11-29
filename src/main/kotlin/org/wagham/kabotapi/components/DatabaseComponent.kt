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

    val buildingsScope = database.buildingsScope
    val charactersScope = database.charactersScope
    val itemsScope = database.itemsScope
    val labelsScope = database.labelsScope
    val playersScope = database.playersScope
    val serverConfigScope = database.serverConfigScope
    val sessionScope = database.sessionScope
    val utilityScope = database.utilityScope

}