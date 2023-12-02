package org.wagham.kabotapi.components

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.serialization.json.Json
import org.wagham.db.models.dto.SessionOutcome
import org.wagham.kabotapi.entities.dto.LevelUpDto
import org.wagham.kabotapi.entities.dto.RegisteredSessionDto

class ExternalGateway(
    config: ApplicationConfig
) {

    private val kabotUrl = config.property("kabot.url").getString()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Sends a message signaling that a new session has been registered in the system.
     *
     * @param guildId the id of the guild where the session was registered.
     * @param sessionId the id of the session.
     */
    suspend fun sendRegisteredSession(guildId: String, sessionId: String) {
        client.post("$kabotUrl/session") {
            contentType(ContentType.Application.Json)
            setBody(RegisteredSessionDto(guildId, sessionId))
        }
    }

    /**
     * Sends a message signaling all the exp rewards awarded after a session.
     *
     * @param guildId the id of the guild.
     * @param outcomes a [List] of [SessionOutcome]
     */
    suspend fun sendLevelUpInfo(guildId: String, outcomes: List<SessionOutcome>) {
        client.post("$kabotUrl/levelUp") {
            contentType(ContentType.Application.Json)
            setBody(LevelUpDto(guildId, outcomes.associate { it.characterId to it.exp.toFloat() }))
        }
    }

}