package org.wagham.kabotapi.services

import org.springframework.stereotype.Service
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.MongoCredentials

@Service
class DatabaseService(
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

    fun getAllGuildItems(guildId: String) = database.getItems(guildId)

}