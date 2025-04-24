package org.wagham.kabotapi.controllers

import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.sse.sse
import korlibs.time.seconds
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.wagham.db.enums.NyxRoles
import org.wagham.kabotapi.components.JWTManager
import org.wagham.kabotapi.configuration.INACTIVE_RATE_LIMIT
import org.wagham.kabotapi.exceptions.JWTException
import org.wagham.kabotapi.logic.FoundryLogic
import org.wagham.kabotapi.utils.authenticatedPost
import kotlin.getValue

fun Routing.foundryController() = route("/foundry") {
	val foundryLogic by inject<FoundryLogic>()
	val jwtManager by inject<JWTManager>()

	rateLimit(RateLimitName(INACTIVE_RATE_LIMIT)) {
		get("/inactive/{instanceUrl}") {
			val instanceUrl = checkNotNull(call.parameters["instanceUrl"]) {
				"Instance URL must not be null"
			}
			delay(10.seconds)
			call.respondRedirect(foundryLogic.startInstance(instanceUrl), permanent = false)
		}
	}

	rateLimit(RateLimitName(INACTIVE_RATE_LIMIT)) {
		get("/inactive/{instanceUrl}/{other}") {
			val instanceUrl = checkNotNull(call.parameters["instanceUrl"]) {
				"Instance URL must not be null"
			}
			delay(10.seconds)
			call.respondRedirect(foundryLogic.startInstance(instanceUrl), permanent = false)
		}
	}

	authenticatedPost("/{instanceId}", roles = setOf(NyxRoles.MANAGE_FOUNDRY)) {
		val instanceId = checkNotNull(call.parameters["instanceId"]) {
			"Instance ID must not be null"
		}
		foundryLogic.stopInstance(instanceId)
		call.respond("ok")
	}

	sse("/info") {
		val jwt = call.parameters["jwt"] ?: throw JWTException("Missing JWT parameter")
		val claims = jwtManager.decodeAndGetClaims(jwt)
		if(!claims.roles.contains(NyxRoles.MANAGE_FOUNDRY)) {
			throw IllegalAccessException("You are not authorized to access this endpoint")
		}
		foundryLogic.subscribeToInfoChannel().collect {
			send(
				data = Json.encodeToString(it),
				event = "foundry-info"
			)
		}
	}
}