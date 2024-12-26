package org.wagham.kabotapi.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.wagham.db.enums.NyxRoles
import org.wagham.kabotapi.components.toJWTClaims
import org.wagham.kabotapi.configuration.AUTH_CTX
import org.wagham.kabotapi.entities.security.JWTClaims
import org.wagham.kabotapi.exceptions.JWTException

/**
 * Extends the default behaviour of [get] by automatically parsing the payload of the JWT from the principal.
 */
fun Route.authenticatedGet(
	path: String,
	ctx: String = AUTH_CTX,
	roles: Set<NyxRoles> = emptySet(),
	block: suspend PipelineContext<Unit, ApplicationCall>.(JWTClaims) -> Unit
): Route = authenticate(ctx) {
	get(path) {
		val claims = call.principal<JWTPrincipal>()?.payload?.toJWTClaims()
			?: throw JWTException("No JWT passed in the request")
		if(roles.isNotEmpty() || claims.roles.containsAll(roles)) {
			block(claims)
		} else {
			throw IllegalAccessException("You are not authorized to access this endpoint")
		}
	}
}

/**
 * Extends the default behaviour of [post] by automatically parsing the payload of the JWT from the principal.
 */
fun Route.authenticatedPost(
	path: String,
	ctx: String = AUTH_CTX,
	roles: Set<NyxRoles> = emptySet(),
	block: suspend PipelineContext<Unit, ApplicationCall>.(JWTClaims) -> Unit
): Route = authenticate(ctx) {
	post(path) {
		val claims = call.principal<JWTPrincipal>()?.payload?.toJWTClaims()
			?: throw JWTException("No JWT passed in the request")
		if(roles.isNotEmpty() || claims.roles.containsAll(roles)) {
			block(claims)
		} else {
			throw IllegalAccessException("You are not authorized to access this endpoint")
		}
	}
}

/**
 * Extends the default behaviour of [get] by automatically parsing the payload of the JWT from the principal.
 */
fun Route.authenticatedDelete(
	path: String,
	ctx: String = AUTH_CTX,
	roles: Set<NyxRoles> = emptySet(),
	block: suspend PipelineContext<Unit, ApplicationCall>.(JWTClaims) -> Unit
): Route = authenticate(ctx) {
	delete(path) {
		val claims = call.principal<JWTPrincipal>()?.payload?.toJWTClaims()
			?: throw JWTException("No JWT passed in the request")
		if(roles.isNotEmpty() || claims.roles.containsAll(roles)) {
			block(claims)
		} else {
			throw IllegalAccessException("You are not authorized to access this endpoint")
		}
	}
}

/**
 * Extends the default behaviour of [put] by automatically parsing the payload of the JWT from the principal.
 */
fun Route.authenticatedPut(
	path: String,
	ctx: String = AUTH_CTX,
	roles: Set<NyxRoles> = emptySet(),
	block: suspend PipelineContext<Unit, ApplicationCall>.(JWTClaims) -> Unit
): Route = authenticate(ctx) {
	put(path) {
		val claims = call.principal<JWTPrincipal>()?.payload?.toJWTClaims()
			?: throw JWTException("No JWT passed in the request")
		if(roles.isNotEmpty() || claims.roles.containsAll(roles)) {
			block(claims)
		} else {
			throw IllegalAccessException("You are not authorized to access this endpoint")
		}
	}
}