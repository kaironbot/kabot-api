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
import org.wagham.kabotapi.exceptions.UnauthorizedException
import org.wagham.kabotapi.logic.DiscordLogic

fun Routing.authController() = route("/auth") {
    val discordLogic by inject<DiscordLogic>()
    val jwtManager by inject<JWTManager>()

    post("/discord") {
        val code = call.receive<DiscordAuthCode>()
        val discordAuthResponse = discordLogic.login(code.code)
        val defaultGuild = discordLogic.getUserGuilds(discordAuthResponse.accessToken).firstOrNull {
            it.id == "1099390660672503980"
        } ?: throw NotFoundException("Cannot find any guild registered in Kairon Bot")
        val discordUser = discordLogic.getCurrentGuildUser(discordAuthResponse.accessToken, defaultGuild.id)
        val response = JwtResponse(
            authToken = jwtManager.generateAuthJWT(JWTClaims(
                discordUser.user?.id ?: throw UnauthorizedException("You are not part of this guild."),
                defaultGuild.id,
                discordAuthResponse.accessToken,
                discordLogic.discordRolesToNyxRoles(discordUser, defaultGuild.id)
            )),
            refreshToken = jwtManager.generateRefreshJWT(JWTRefreshClaims(
                discordUser.user.id,
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
            val discordUser = discordLogic.getCurrentGuildUser(discordRefreshResponse.accessToken, "1099390660672503980")
            val response = JwtResponse(
                authToken = jwtManager.generateAuthJWT(JWTClaims(
                    discordUser.user?.id ?: throw UnauthorizedException("You are not part of this guild."),
                    claims.guildId,
                    discordRefreshResponse.accessToken,
                    discordLogic.discordRolesToNyxRoles(discordUser, "1099390660672503980")
                )),
                refreshToken = jwtManager.generateRefreshJWT(JWTRefreshClaims(
                    discordUser.user.id,
                    claims.guildId,
                    discordRefreshResponse.refreshToken,
                ))
            )
            call.respond(response)
        }
    }
}