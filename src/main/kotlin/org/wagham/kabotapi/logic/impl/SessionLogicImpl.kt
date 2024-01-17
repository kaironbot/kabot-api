package org.wagham.kabotapi.logic.impl

import org.wagham.db.models.dto.SessionOutcome
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.components.ExternalGateway
import org.wagham.kabotapi.entities.dto.SessionRegistrationDto
import org.wagham.kabotapi.logic.SessionLogic
import java.util.*

class SessionLogicImpl(
    private val database: DatabaseComponent,
    private val gateway: ExternalGateway
) : SessionLogic {

    override suspend fun insertSession(guildId: String, responsibleId: String, sessionInfo: SessionRegistrationDto) {
        val sessionId = UUID.randomUUID().toString()
        database.sessionScope.insertSession(
            guildId,
            sessionId,
            sessionInfo.masterId,
            sessionInfo.masterReward,
            sessionInfo.title,
            Date(sessionInfo.date),
            sessionInfo.outcomes,
            sessionInfo.labels,
            responsibleId
        ).also {
            if(!it.committed) {
                throw IllegalStateException(it.exception?.message)
            }
        }
        gateway.sendRegisteredSession(guildId, sessionId)
        gateway.sendLevelUpInfo(
            guildId,
            sessionInfo.outcomes + SessionOutcome(sessionInfo.masterId, sessionInfo.masterReward, false)
        )
    }


}