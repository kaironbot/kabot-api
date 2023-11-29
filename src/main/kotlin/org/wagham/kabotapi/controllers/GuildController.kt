package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.wagham.kabotapi.entities.GuildMember
import org.wagham.kabotapi.logic.DiscordLogic
import org.wagham.kabotapi.utils.authenticatedGet
import java.lang.IllegalStateException

fun Routing.guildController() = route("/guild") {
    val discordLogic by inject<DiscordLogic>()

    authenticatedGet("/current/member") {
        val guildMember = discordLogic.getCurrentGuildUser(
            it.discordAuthToken,
            it.guildId
        ).let { member ->
            if(member.user == null) {
                val globalUser = discordLogic.getCurrentGlobalUser(it.discordAuthToken)
                member.copy(user = globalUser)
            } else member
        }.let { member ->
            GuildMember(
                member.user?.id ?: throw IllegalStateException("Cannot find global user"),
                member.nick ?: member.user.username,
                member.avatar ?: member.user.avatar,
                member.roles ?: emptySet(),
                member.pending ?: false
            )
        }
        call.respond(guildMember)
    }
}