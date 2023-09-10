package org.wagham.kabotapi.logic

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.springframework.stereotype.Component
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.configuration.DiscordConfiguration
import org.wagham.kabotapi.entities.discord.DiscordAuthResponse
import org.wagham.kabotapi.entities.discord.DiscordGlobalUser
import org.wagham.kabotapi.entities.discord.DiscordGuildUser
import org.wagham.kabotapi.entities.discord.DiscordPartialGuild
import org.wagham.kabotapi.exceptions.UnauthorizedException

@Component
class DiscordLogic(
    private val databaseComponent: DatabaseComponent,
    private val discordConfiguration: DiscordConfiguration,
    private val client: HttpClient,
    private val objectMapper: ObjectMapper
) {

    suspend fun login(code: String) = client.post("${discordConfiguration.apiEndpoint}/oauth2/token") {
            setBody(FormDataContent(Parameters.build {
                append("client_id", discordConfiguration.clientId)
                append("client_secret", discordConfiguration.clientSecret)
                append("grant_type", "authorization_code")
                append("scope", "identify")
                append("redirect_uri", discordConfiguration.redirectUrl)
                append("code", code)
            }))
        }.bodyOrUnauthorized<DiscordAuthResponse>(objectMapper, "Cannot obtain discord token")

    suspend fun refreshDiscordToken(refreshToken: String) = client.post("${discordConfiguration.apiEndpoint}/oauth2/token") {
        setBody(FormDataContent(Parameters.build {
            append("client_id", discordConfiguration.clientId)
            append("client_secret", discordConfiguration.clientSecret)
            append("grant_type", "refresh_token")
            append("refresh_token", refreshToken)
        }))
    }.bodyOrUnauthorized<DiscordAuthResponse>(objectMapper, "Cannot refresh discord token")

    suspend fun getCurrentGlobalUser(discordJwt: String) = client.get("${discordConfiguration.apiEndpoint}/users/@me") {
        bearerAuth(discordJwt)
    }.bodyOrUnauthorized<DiscordGlobalUser>(objectMapper, "Cannot get global discord user")

    suspend fun getCurrentGuildUser(discordJwt: String, guildId: String) =
        client.get("${discordConfiguration.apiEndpoint}/users/@me/guilds/${guildId}/member") {
            bearerAuth(discordJwt)
        }.bodyOrUnauthorized<DiscordGuildUser>(objectMapper, "Cannot get member in guild $guildId")

    suspend fun getUserGuilds(discordJwt: String) = client.get("${discordConfiguration.apiEndpoint}/users/@me/guilds") {
        bearerAuth(discordJwt)
    }.bodyOrUnauthorized<List<DiscordPartialGuild>>(objectMapper, "Cannot get guilds for current user").let { guilds ->
        val registeredGuilds = databaseComponent.registeredGuilds
        guilds.filter { registeredGuilds.contains(it.id) }
    }

}

suspend inline fun <reified T> HttpResponse.bodyOrUnauthorized(mapper: ObjectMapper, msg: String): T = try {
    mapper.readValue<T>(bodyAsText())
} catch (e: Exception) {
    throw UnauthorizedException(msg)
}