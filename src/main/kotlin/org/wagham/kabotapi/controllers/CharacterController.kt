package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject
import org.wagham.db.models.Errata
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.utils.authenticatedGet
import org.wagham.kabotapi.utils.authenticatedPost

fun Routing.characterController() = route("/character") {
    val characterLogic by inject<CharacterLogic>()

    authenticatedGet("/active") {
        val characters = characterLogic.getAllActiveCharacters(it.guildId).toList()
        call.respond(characters)
    }

    authenticatedGet("/active/withPlayer") {
        val characters = characterLogic.getAllActiveCharactersWithPlayer(it.guildId).toList()
        call.respond(characters)
    }

    authenticatedGet("/current") {
        val characters = characterLogic.getActiveCharacters(it.guildId, it.userId).toList()
        call.respond(characters)
    }

    authenticatedGet("/{characterId}") {
        val characterId = checkNotNull(call.parameters["characterId"]) {
            "Character Id must not be null"
        }
        val character = characterLogic.getCharacter(it.guildId, it.userId, characterId)
        call.respond(character)
    }

    authenticatedPost("/{characterId}/errata") {
        val errata = call.receive<Errata>()
        val characterId = checkNotNull(call.parameters["characterId"]) {
            "Character Id must not be null"
        }
        val response = characterLogic.addErrata(it.guildId, characterId, errata)
        call.respond(response)
    }
}