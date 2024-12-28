package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.wagham.kabotapi.logic.UtilitiesLogic
import org.wagham.kabotapi.utils.authenticatedGet

fun Routing.utilitiesController() = route("/utils") {
	val utilitiesLogic by inject<UtilitiesLogic>()

	authenticatedGet("/expTable") {
		call.respond(utilitiesLogic.getExpTable(it.guildId))
	}
}