package org.wagham.kabotapi.logic

import org.wagham.db.models.GenericSession
import org.wagham.db.models.Player
import org.wagham.kabotapi.entities.PaginatedList
import org.wagham.kabotapi.entities.dto.SessionRegistrationDto

interface SessionLogic {

    /**
     * Registers a new] in the guild specified as parameter. It also sends a message on the gateway with
     * KaironBot to communicate that a new session has been registered.
     *
     * @param guildId the id of the guild where to register the session.
     * @param responsibleId the id of the Discord id that inserted the session.
     * @param sessionInfo a [SessionRegistrationDto] instance.
     */
    suspend fun insertSession(guildId: String, responsibleId: String, sessionInfo: SessionRegistrationDto)

    /**
     * Deletes a previously registered session and updates the exp of the participants.
     *
     * @param guildId the id of the guild where the session was registered.
     * @param sessionId the id of the session to delete.
     * @param masterReward the exp rewarded to the master for the session.
     */
    suspend fun deleteSession(guildId: String, sessionId: String, masterReward: Int)

    /**
     * Returns all the sessions in a guild, ordered by date descending, with support for pagination.
     *
     * @param guildId the id of the guild where to retrieve the sessions.
     * @param limit the maximum numbers of elements to be included in the page.
     * @param skip the number of elements to skip to go to the starting element of the page.
     * @return a [PaginatedList] of [GenericSession] parametrized by [Player]
     */
    suspend fun getPaginatedSessions(guildId: String, limit: Int? = null, skip: Int? = null): PaginatedList<GenericSession<Player>>

    /**
     * Returns the number of sessions registered in a guild.
     *
     * @param guildId the id of the guild.
     * @return the number of sessions registered in the guild.
     */
    suspend fun getSessionsCount(guildId: String): Long

}