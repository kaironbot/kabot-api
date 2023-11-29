package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import org.koin.ktor.ext.inject
import org.wagham.db.enums.NyxRoles
import org.wagham.kabotapi.entities.SuccessResponse
import org.wagham.kabotapi.entities.dto.SessionRegistrationDto
import org.wagham.kabotapi.logic.SessionLogic
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
        call.respond(SuccessResponse(true))
    }
}