package org.wagham.kabotapi.controllers

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.kabotapi.services.DatabaseService

@RestController
@RequestMapping("/api/items")
class ItemController (
    val databaseService: DatabaseService
) {

    @GetMapping
    fun getItems(@RequestHeader("Guild-ID") guildId: String) = mono {
        databaseService.getAllGuildItems(guildId)
            .catch {e ->
                if (e is InvalidGuildException)
                    throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            }
    }

}