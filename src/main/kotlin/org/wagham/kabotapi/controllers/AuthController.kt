package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.wagham.kabotapi.components.JWTManager
import org.wagham.kabotapi.components.toJWTRefreshClaims
import org.wagham.kabotapi.configuration.REFRESH_CTX
import org.wagham.kabotapi.entities.security.JwtResponse
import org.wagham.kabotapi.entities.discord.DiscordAuthCode
import org.wagham.kabotapi.entities.security.JWTClaims
import org.wagham.kabotapi.entities.security.JWTRefreshClaims
import org.wagham.kabotapi.exceptions.JWTException
import org.wagham.kabotapi.exceptions.NotFoundException
import org.wagham.kabotapi.logic.DiscordLogic

fun Routing.authController() = route("/auth") {
    val discordLogic by inject<DiscordLogic>()
    val jwtManager by inject<JWTManager>()

    post("/discord") {
        val code = call.receive<DiscordAuthCode>()
        val discordAuthResponse = discordLogic.login(code.code)
        val defaultGuild = discordLogic.getUserGuilds(discordAuthResponse.accessToken).firstOrNull{
            it.id == "1099390660672503980"
        } ?: throw NotFoundException("Cannot find any guild registered in Kairon Bot")
        val discordUser = discordLogic.getCurrentGlobalUser(discordAuthResponse.accessToken)
        val response = JwtResponse(
            authToken = jwtManager.generateAuthJWT(JWTClaims(
                discordUser.id,
                defaultGuild.id,
                discordAuthResponse.accessToken,
            )),
            refreshToken = jwtManager.generateRefreshJWT(JWTRefreshClaims(
                discordUser.id,
                defaultGuild.id,
                discordAuthResponse.refreshToken,
            ))
        )
        call.respond(response)
    }

    authenticate(REFRESH_CTX) {
        post("/refresh") {
            val claims = call.principal<JWTPrincipal>()?.payload?.toJWTRefreshClaims()
                ?: throw JWTException("No JWT passed in the request")
            val discordRefreshResponse = discordLogic.refreshDiscordToken(claims.discordRefreshToken)
            val discordUser = discordLogic.getCurrentGlobalUser(discordRefreshResponse.accessToken)
            val response = JwtResponse(
                authToken = jwtManager.generateAuthJWT(JWTClaims(
                    discordUser.id,
                    claims.guildId,
                    discordRefreshResponse.accessToken,
                )),
                refreshToken = jwtManager.generateRefreshJWT(JWTRefreshClaims(
                    discordUser.id,
                    claims.guildId,
                    discordRefreshResponse.refreshToken,
                ))
            )
            call.respond(response)
        }
    }
}