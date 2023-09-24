package org.wagham.kabotapi.controllers

import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.kabotapi.dao.CharacterDAO
import org.wagham.kabotapi.security.JwtAuthenticationToken
import java.lang.Exception

@RestController
@RequestMapping("/characters")
class CharacterController(
    val characterDAO: CharacterDAO
) {
    @GetMapping
    fun getCharacters(@RequestHeader("Guild-ID") guildId: String) =
        try {
            characterDAO.getAllCharacters(guildId)
        } catch (e: Exception) {
            if (e is InvalidGuildException)
                throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            else
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }

    @GetMapping("/active")
    fun getActiveCharacter(
        @RequestHeader("Guild-ID") guildId: String,
        @RequestParam(required = true) player: String,
    ) = mono {
        try {
            characterDAO.getActiveCharacter(guildId, player)
        } catch (e: Exception) {
            when(e) {
                is InvalidGuildException -> throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
                is NoActiveCharacterException -> throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
                else -> throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
            }
        }
    }

    @GetMapping("/current")
    fun getCurrentCharacters(jwtAuth: JwtAuthenticationToken) =
        characterDAO.getActiveCharacters(jwtAuth.claims.guildId, jwtAuth.claims.userId)

}