package org.wagham.kabotapi.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.kabotapi.dao.ItemDAO
import java.lang.Exception

@RestController
@RequestMapping("/api/item")
class ItemController (
    val itemDAO: ItemDAO
) {

    @GetMapping
    fun getItems(@RequestHeader("Guild-ID") guildId: String) =
        try {
            itemDAO.getAllGuildItems(guildId)
        } catch (e: Exception) {
            if (e is InvalidGuildException)
                throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            else
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }

}