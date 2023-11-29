package org.wagham.kabotapi.logic

import org.wagham.kabotapi.entities.dto.SessionRegistrationDto

interface SessionLogic {

    /**
     * Registers a new session in the guild specified as parameter. It also sends a message on the gateway with
     * KaironBot to communicate that a new session has been registered.
     *
     * @param guildId the id of the guild where to register the session.
     * @param responsibleId the id of the Discord id that inserted the session.
     * @param sessionInfo a [SessionRegistrationDto] instance.
     */
    suspend fun insertSession(guildId: String, responsibleId: String, sessionInfo: SessionRegistrationDto)

}