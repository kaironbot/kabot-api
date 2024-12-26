package org.wagham.kabotapi.controllers

import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.wagham.db.enums.NyxRoles
import org.wagham.db.models.Errata
import org.wagham.db.models.embed.CharacterToken
import org.wagham.kabotapi.entities.StatusResponse
import org.wagham.kabotapi.entities.dto.items.UpdateInventoryDto
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.utils.FileUtils
import org.wagham.kabotapi.utils.MimeType
import org.wagham.kabotapi.utils.authenticatedGet
import org.wagham.kabotapi.utils.authenticatedPost
import org.wagham.kabotapi.utils.guard
import java.time.Duration

fun Routing.characterController() = route("/character") {
	val characterLogic by inject<CharacterLogic>()
	val tokenCache = Caffeine.newBuilder()
		.maximumSize(5_000)
		.expireAfterAccess(Duration.ofHours(1))
		.build<String, CharacterToken>()

	authenticatedGet("/active") {
		val characters = characterLogic.getAllActiveCharacters(it.guildId)
		call.respond(characters)
	}

	authenticatedGet("/active/withPlayer") {
		val characters = characterLogic.getAllActiveCharactersWithPlayer(it.guildId)
		call.respond(characters)
	}

	authenticatedGet("/current") {
		val characters = characterLogic.getActiveCharacters(it.guildId, it.userId)
		call.respond(characters)
	}

	authenticatedGet("/forSelf") {
		val characters = characterLogic.getCharactersForPlayer(it.guildId, it.userId)
		call.respond(characters)
	}

	authenticatedGet("/{characterId}") {
		val characterId = checkNotNull(call.parameters["characterId"]) {
			"Character Id must not be null"
		}
		val character = characterLogic.getCharacter(it.guildId, it.userId, characterId)
		call.respond(character)
	}

	authenticatedPost("/{characterId}/errata") {
		val errata = call.receive<Errata>()
		val characterId = checkNotNull(call.parameters["characterId"]) {
			"Character Id must not be null"
		}
		val response = characterLogic.addErrata(it.guildId, characterId, errata)
		call.respond(response)
	}

	authenticatedPost("/{characterId}/inventory") {
		val characterId = checkNotNull(call.parameters["characterId"]) {
			"Character Id must not be null"
		}
		val payload = call.receive<UpdateInventoryDto>()
		if (payload.operation == UpdateInventoryDto.Companion.InventoryUpdate.ASSIGN) {
			guard(it.roles.contains(NyxRoles.MANAGE_CHARACTERS)) {
				"You are not allowed to assign items"
			}
		}
		characterLogic.updateInventory(it.guildId, it.userId, characterId, payload)
		call.respond(StatusResponse(true))
	}

	authenticatedGet("/{characterId}/token") {
		val characterId = checkNotNull(call.parameters["characterId"]) { "Character Id must not be null" }
		val token = tokenCache.getIfPresent("${it.guildId}:$characterId") ?:
		characterLogic.getCharacterToken(it.guildId, characterId).also { token ->
			tokenCache.put("${it.guildId}:$characterId", token)
		}
		call.respond(token)
	}

	authenticatedPost("/{characterId}/token") {
		val characterId = checkNotNull(call.parameters["characterId"]) { "Character Id must not be null" }
		val tokenPart = call.receiveMultipart().readPart()?.takeIf { part -> part is PartData.FileItem } as? PartData.FileItem
			?: throw IllegalArgumentException("No file uploaded")
		val imageBytes = tokenPart.streamProvider().readAllBytes()
		tokenPart.dispose()

		val mimeType = FileUtils.inferMimeType(imageBytes)
		if(!MimeType.isImage(mimeType)) {
			throw IllegalStateException("Type not supported: ${mimeType.mimeType}")
		}
		if(imageBytes.size > 256_000) {
			throw IllegalArgumentException("Image file is too big")
		}
		val result = characterLogic.setCharacterToken(
			it.guildId,
			it.userId,
			characterId,
			imageBytes,
			mimeType
		)

		if(result) {
			tokenCache.invalidate("${it.guildId}:$characterId")
		}
		call.respond(StatusResponse(result))
	}
}