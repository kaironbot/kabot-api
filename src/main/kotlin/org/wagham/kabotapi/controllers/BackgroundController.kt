package org.wagham.kabotapi.controllers

import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.kabotapi.dao.BackgroundDAO
import java.lang.Exception

@RestController
@RequestMapping("/api/background")
class BackgroundController(
    val backgroundDAO: BackgroundDAO
){

    @GetMapping
    fun getBackgrounds(@RequestHeader("Guild-ID") guildId: String) =
        try {
            backgroundDAO.getAllGuildBackgrounds(guildId)
        } catch (e: Exception) {
            if (e is InvalidGuildException)
                throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            else
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }

}