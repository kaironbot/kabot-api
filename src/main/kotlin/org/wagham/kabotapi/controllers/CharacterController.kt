package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.utils.authenticatedGet

fun Routing.characterController() = route("/character") {
    val characterLogic by inject<CharacterLogic>()
    val mapper = ObjectMapper()

    authenticatedGet("/active") {
        val characters = characterLogic.getAllActiveCharacters(it.guildId).toList()
        call.respond(mapper.writeValueAsString(characters))
    }

    authenticatedGet("/active/withPlayer") {
        val characters = characterLogic.getAllActiveCharactersWithPlayer(it.guildId).toList()
        call.respond(mapper.writeValueAsString(characters))
    }

    authenticatedGet("/current") {
        val characters = characterLogic.getActiveCharacters(it.guildId, it.userId).toList()
        call.respond(mapper.writeValueAsString(characters))
    }
}