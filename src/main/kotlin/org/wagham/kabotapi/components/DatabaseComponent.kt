package org.wagham.kabotapi.components

import com.mongodb.reactivestreams.client.ClientSession
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.client.TransactionResult
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

    val charactersScope = database.charactersScope
    val itemsScope = database.itemsScope
    val labelsScope = database.labelsScope
    val playersScope = database.playersScope
    val serverConfigScope = database.serverConfigScope
    val sessionScope = database.sessionScope
    val utilityScope = database.utilityScope
    val transactionsScope = database.characterTransactionsScope

    suspend fun transaction(
        guildId: String,
        retries: Long = 3,
        block: suspend (ClientSession) -> Boolean
    ): TransactionResult = database.transaction(guildId, retries, block)

}