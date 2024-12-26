package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.wagham.kabotapi.logic.PlayerLogic
import org.wagham.kabotapi.utils.authenticatedPost

fun Routing.playerController() = route("/player") {
	val playerLogic by inject<PlayerLogic>()

	authenticatedPost("/byIds") {
		val playerIds = call.receive<List<String>>()
		call.respond(playerLogic.getPlayers(it.guildId, playerIds))
	}
}