package org.wagham.kabotapi.controllers

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.wagham.kabotapi.entities.GuildMember
import org.wagham.kabotapi.logic.DiscordLogic
import org.wagham.kabotapi.security.JwtAuthenticationToken
import java.lang.IllegalStateException

@RestController
@RequestMapping("/guild")
class GuildController(
    private val discordLogic: DiscordLogic
) {

    @GetMapping("/current/member")
    suspend fun getCurrentGuildMember(jwtAuth: JwtAuthenticationToken): GuildMember {
        return discordLogic.getCurrentGuildUser(
            jwtAuth.claims.discordAuthToken,
            jwtAuth.claims.guildId
        ).let { member ->
            if(member.user == null) {
                val globalUser = discordLogic.getCurrentGlobalUser(jwtAuth.claims.discordAuthToken)
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
    }

}