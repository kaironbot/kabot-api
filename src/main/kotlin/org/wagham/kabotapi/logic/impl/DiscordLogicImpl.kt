package org.wagham.kabotapi.logic.impl

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.wagham.db.enums.NyxRoles
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.entities.config.DiscordConfig
import org.wagham.kabotapi.entities.discord.DiscordAuthResponse
import org.wagham.kabotapi.entities.discord.DiscordGlobalUser
import org.wagham.kabotapi.entities.discord.DiscordGuildUser
import org.wagham.kabotapi.entities.discord.DiscordPartialGuild
import org.wagham.kabotapi.exceptions.UnauthorizedException
import org.wagham.kabotapi.logic.DiscordLogic

class DiscordLogicImpl(
	private val database: DatabaseComponent,
	private val discordConfig: DiscordConfig,
) : DiscordLogic {

	private val client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
			})
		}
	}

	private suspend inline fun <reified T> HttpResponse.bodyOrUnauthorized(msg: String): T = try {
		body<T>()
	} catch (e: Exception) {
		throw UnauthorizedException(msg)
	}

	override suspend fun login(code: String) = client.post("${discordConfig.apiEndpoint}/oauth2/token") {
		setBody(FormDataContent(Parameters.build {
			append("client_id", discordConfig.clientId)
			append("client_secret", discordConfig.clientSecret)
			append("grant_type", "authorization_code")
			append("scope", "identify")
			append("redirect_uri", discordConfig.redirectUrl)
			append("code", code)
		}))
	}.bodyOrUnauthorized<DiscordAuthResponse>("Cannot obtain discord token")

	override suspend fun refreshDiscordToken(refreshToken: String) = client.post("${discordConfig.apiEndpoint}/oauth2/token") {
		setBody(FormDataContent(Parameters.build {
			append("client_id", discordConfig.clientId)
			append("client_secret", discordConfig.clientSecret)
			append("grant_type", "refresh_token")
			append("refresh_token", refreshToken)
		}))
	}.bodyOrUnauthorized<DiscordAuthResponse>("Cannot refresh discord token")

	override suspend fun getCurrentGlobalUser(discordJwt: String) = client.get("${discordConfig.apiEndpoint}/users/@me") {
		bearerAuth(discordJwt)
	}.bodyOrUnauthorized<DiscordGlobalUser>("Cannot get global discord user")

	override suspend fun getCurrentGuildUser(discordJwt: String, guildId: String) =
		client.get("${discordConfig.apiEndpoint}/users/@me/guilds/${guildId}/member") {
			bearerAuth(discordJwt)
		}.bodyOrUnauthorized<DiscordGuildUser>("Cannot get member in guild $guildId")

	override suspend fun getUserGuilds(discordJwt: String) = client.get("${discordConfig.apiEndpoint}/users/@me/guilds") {
		bearerAuth(discordJwt)
	}.bodyOrUnauthorized<List<DiscordPartialGuild>>("Cannot get guilds for current user").let { guilds ->
		val registeredGuilds = database.registeredGuilds
		guilds.filter { registeredGuilds.contains(it.id) }
	}

	override suspend fun discordRolesToNyxRoles(user: DiscordGuildUser, guildId: String): Set<NyxRoles> {
		val nyxConfig = database.serverConfigScope.getNyxConfig(guildId)
		return user.roles?.flatMap { nyxConfig.roleConfig[it] ?: emptySet() }?.toSet() ?: emptySet()
	}

}