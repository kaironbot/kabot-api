package org.wagham.kabotapi.logic.impl

import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.entities.dto.SessionRegistrationDto
import org.wagham.kabotapi.logic.SessionLogic
import java.util.*

class SessionLogicImpl(
    private val database: DatabaseComponent
) : SessionLogic {

    override  suspend fun insertSession(guildId: String, sessionInfo: SessionRegistrationDto) {
        database.sessionScope.insertSession(
            guildId,
            sessionInfo.masterId,
            sessionInfo.masterReward,
            sessionInfo.title,
            Date(sessionInfo.date),
            sessionInfo.outcomes,
            sessionInfo.labels
        ).also {
            if(!it.committed) {
                throw IllegalStateException(it.exception?.message)
            }
        }
    }


}