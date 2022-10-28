package org.wagham.kabotapi.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.kabotapi.dao.SubclassDAO
import org.wagham.kabotapi.dao.UtilityDAO
import java.lang.Exception

@RestController
@RequestMapping("/api/utility")
class UtilityController(
    val utilityDAO: UtilityDAO
) {

    @GetMapping("/expTable")
    suspend fun getExpTable(@RequestHeader("Guild-ID") guildId: String) =
        try {
            utilityDAO.getExpTable(guildId)
        } catch (e: Exception) {
            if (e is InvalidGuildException)
                throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            else
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
}