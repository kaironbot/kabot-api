package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.wagham.db.enums.NyxRoles
import org.wagham.kabotapi.entities.StatusResponse
import org.wagham.kabotapi.entities.dto.CountDto
import org.wagham.kabotapi.entities.dto.SessionRegistrationDto
import org.wagham.kabotapi.logic.SessionLogic
import org.wagham.kabotapi.utils.authenticatedDelete
import org.wagham.kabotapi.utils.authenticatedGet
import org.wagham.kabotapi.utils.authenticatedPost

fun Routing.sessionController() = route("/session") {
    val sessionLogic by inject<SessionLogic>()

    authenticatedPost("", roles = setOf(NyxRoles.MANAGE_SESSIONS)) {
        val body = call.receive<SessionRegistrationDto>()
        sessionLogic.insertSession(
            it.guildId,
            it.userId,
            body
        )
        call.respond(StatusResponse(true))
    }

    authenticatedDelete("/{sessionId}", roles = setOf(NyxRoles.MANAGE_SESSIONS)) {
        val sessionId = checkNotNull(call.parameters["sessionId"]) { "Session Id must not be null" }
        val masterReward = call.request.queryParameters["masterReward"]?.toInt() ?: 0
        sessionLogic.deleteSession(it.guildId, sessionId, masterReward)
        call.respond(StatusResponse(true))
    }

    authenticatedGet("") {
        val limit = call.request.queryParameters["limit"]?.toInt()
        val nextAt = call.request.queryParameters["nextAt"]?.toInt()
        val list = sessionLogic.getPaginatedSessions(it.guildId, limit, nextAt)
        call.respond(list)
    }

    authenticatedGet("/count") {
        val count = sessionLogic.getSessionsCount(it.guildId)
        call.respond(CountDto(count))
    }
}