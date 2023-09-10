package org.wagham.kabotapi.controllers

import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.wagham.kabotapi.entities.JwtResponse
import org.wagham.kabotapi.entities.discord.DiscordAuthCode
import org.wagham.kabotapi.logic.DiscordLogic
import org.wagham.kabotapi.security.JwtDetails
import org.wagham.kabotapi.security.JwtUtils
import org.wagham.kabotapi.security.Roles
import java.lang.IllegalStateException

@RestController
@RequestMapping("/login")
class LoginController(
    private val discordLogic: DiscordLogic,
    private val jwtUtils: JwtUtils
) {

    @PostMapping("/discord")
    suspend fun authenticateThroughDiscord(
        @RequestBody code: DiscordAuthCode
    ): JwtResponse {
        val discordAuthResponse = discordLogic.login(code.code)
        val defaultGuild = discordLogic.getUserGuilds(discordAuthResponse.accessToken).firstOrNull{
            it.id == "1099390660672503980"
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find any guild registered in Kairon Bot")
        val discordUser = discordLogic.getCurrentGlobalUser(discordAuthResponse.accessToken)
        val jwtDetails = JwtDetails(
            setOf(SimpleGrantedAuthority(Roles.ROLE_USER)),
            discordUser.id,
            defaultGuild.id,
            discordAuthResponse.accessToken,
            discordAuthResponse.refreshToken
        )
        return JwtResponse(
            authToken = jwtUtils.createJWT(jwtDetails, discordAuthResponse.expiration - 10),
            refreshToken = jwtUtils.createRefreshJWT(jwtDetails)
        )
    }

    @PostMapping("/refresh")
    suspend fun refreshDiscordToken(
        @RequestHeader("Refresh-Token") refreshToken: String
    ): JwtResponse {
        val refreshDetails = jwtUtils.extractRefreshDetailsFromToken(refreshToken)
        val discordRefreshResponse = discordLogic.refreshDiscordToken(
            refreshDetails.discordRefreshToken ?: throw IllegalStateException("Something went wrong")
        )
        val discordUser = discordLogic.getCurrentGlobalUser(discordRefreshResponse.accessToken)
        val jwtDetails = JwtDetails(
            setOf(SimpleGrantedAuthority(Roles.ROLE_USER)),
            discordUser.id,
            refreshDetails.guildId,
            discordRefreshResponse.accessToken,
            discordRefreshResponse.refreshToken
        )
        return JwtResponse(
            authToken = jwtUtils.createJWT(jwtDetails, discordRefreshResponse.expiration - 10),
            refreshToken = jwtUtils.createRefreshJWT(jwtDetails)
        )
    }


}