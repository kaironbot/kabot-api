package org.wagham.kabotapi.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.wagham.kabotapi.components.toJWTClaims
import org.wagham.kabotapi.entities.security.JWTClaims
import org.wagham.kabotapi.exceptions.JWTException

/**
 * Extends the default behaviour of [get] by automatically parsing the payload of the JWT from the principal.
 */
fun Route.authenticatedGet(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(JWTClaims) -> Unit
): Route = get(path) {
    val claims = call.principal<JWTPrincipal>()?.payload?.toJWTClaims()
        ?: throw JWTException("No JWT passed in the request")
    body(claims)
}