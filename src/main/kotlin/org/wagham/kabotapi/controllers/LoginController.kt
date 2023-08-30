package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.wagham.kabotapi.configuration.DiscordConfiguration
import org.wagham.kabotapi.entities.discord.DiscordAuthCode
import org.wagham.kabotapi.entities.discord.DiscordAuthPayload
import org.wagham.kabotapi.entities.discord.DiscordAuthResponse

@RestController
@RequestMapping("/api/login")
class LoginController(
    private val discordConfiguration: DiscordConfiguration,
    private val client: HttpClient,
    private val objectMapper: ObjectMapper
) {

    @PostMapping("/token/discord")
    suspend fun authenticateThroughDiscord(
        @RequestBody code: DiscordAuthCode
    ) {
        val discordAuthPayload = DiscordAuthPayload(
            discordConfiguration.clientId,
            discordConfiguration.clientSecret,
            "authorization_code",
            code.code,
            discordConfiguration.redirectUrl
        )
        val discordAuthResponse = client.post("${discordConfiguration.apiEndpoint}/oauth2/token") {
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody(objectMapper.writeValueAsString(discordAuthPayload))
        }.bodyAsText().let {
            try {
                objectMapper.readValue<DiscordAuthResponse>(it)
            } catch (e: Exception) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot obtain discord token")
            }
        }
    }

}