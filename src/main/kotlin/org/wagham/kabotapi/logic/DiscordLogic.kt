package org.wagham.kabotapi.logic

import org.wagham.kabotapi.entities.discord.DiscordAuthResponse
import org.wagham.kabotapi.entities.discord.DiscordGlobalUser
import org.wagham.kabotapi.entities.discord.DiscordGuildUser
import org.wagham.kabotapi.entities.discord.DiscordPartialGuild

interface DiscordLogic {

    /**
     * Completes the Discord oauth authentication flow with the [code] received on the browser by the user.
     *
     * @param code the code received by the user on the browser.
     * @return a [DiscordAuthResponse] if the authorization completes successfully.
     * @throws [org.wagham.kabotapi.exceptions.UnauthorizedException] if it was not possible to complete the authentication
     * flow.
     */
    suspend fun login(code: String): DiscordAuthResponse

    /**
     * Generate a new Discord authentication token using the refresh token.
     *
     * @param refreshToken the refresh token for the refresh API.
     * @return a [DiscordAuthResponse] if the refresh completes successfully.
     * @throws [org.wagham.kabotapi.exceptions.UnauthorizedException] if it was not possible to complete the refresh
     * flow.
     */
    suspend fun refreshDiscordToken(refreshToken: String): DiscordAuthResponse

    /**
     * Retrieves the global Discord user, given the Discord API JWT.
     * A global user contains no information related to the guilds they belong to.
     *
     * @param discordJwt a valid Discord API JWT.
     * @return a [DiscordGlobalUser].
     * @throws [org.wagham.kabotapi.exceptions.UnauthorizedException] if it was not possible to retrieve the user.
     */
    suspend fun getCurrentGlobalUser(discordJwt: String): DiscordGlobalUser

    /**
     * Retrieves the Discord user with all the information related to a specific guild, given the Discord API JWT.
     *
     * @param discordJwt a valid Discord API JWT.
     * @param guildId the Discord id of the guild where to search the user.
     * @return a [DiscordGuildUser].
     * @throws [org.wagham.kabotapi.exceptions.UnauthorizedException] if it was not possible to retrieve the user.
     */
    suspend fun getCurrentGuildUser(discordJwt: String, guildId: String): DiscordGuildUser

    /**
     * Retrieves all the guilds that the current user belongs to where KaironBot is registered.
     *
     * @param discordJwt a valid Discord API JWT.
     * @return a [List] containing all the [DiscordPartialGuild] of the user where KaironBot is present.
     * @throws [org.wagham.kabotapi.exceptions.UnauthorizedException] if it was not possible to retrieve the guilds.
     */
    suspend fun getUserGuilds(discordJwt: String): List<DiscordPartialGuild>

}