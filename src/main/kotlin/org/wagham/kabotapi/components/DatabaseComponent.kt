package org.wagham.kabotapi.components

import org.springframework.stereotype.Component
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.MongoCredentials

@Component
class DatabaseComponent(
    adminUser: String = System.getenv("DB_ADMIN_USER"),
    adminPwd: String = System.getenv("DB_ADMIN_PWD"),
    databaseName: String = System.getenv("DB_ADMIN_NAME"),
    databaseIp: String = System.getenv("DB_ADMIN_IP"),
    databasePort: Int = System.getenv("DB_ADMIN_PORT").toInt()
) {
    val database = KabotMultiDBClient(
        MongoCredentials(
            "ADMIN",
            adminUser,
            adminPwd,
            databaseName,
            databaseIp,
            databasePort
        )
    )

    val backgroundsScope
        get() = database.backgroundsScope
    val charactersScope
        get() = database.charactersScope
    val featsScope
        get() = database.featsScope
    val itemsScope
        get() = database.itemsScope
    val playersScope
        get() = database.playersScope
    val spellsScope
        get() = database.spellsScope
    val subclassesScope
        get() = database.subclassesScope

}