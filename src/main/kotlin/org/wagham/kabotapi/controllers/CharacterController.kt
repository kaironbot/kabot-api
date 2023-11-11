package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject
import org.wagham.db.models.Character
import org.wagham.kabotapi.configuration.AUTH_CTX
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.utils.authenticatedGet


fun Routing.characterController() = route("/character") {
    val characterLogic by inject<CharacterLogic>()
    val mapper = ObjectMapper()

    authenticate(AUTH_CTX) {
        authenticatedGet("/active") {
            val characters = characterLogic.getActiveCharacters(it.guildId, it.userId).toList()
            call.respond(mapper.writeValueAsString(characters))
        }
    }
}