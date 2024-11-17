package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.koin.ktor.ext.inject
import org.wagham.db.enums.NyxRoles
import org.wagham.db.models.Item
import org.wagham.kabotapi.entities.dto.items.ItemUpdate
import org.wagham.db.models.embed.LabelStub
import org.wagham.kabotapi.entities.StatusResponse
import org.wagham.kabotapi.entities.dto.ListOfIdsDto
import org.wagham.kabotapi.logic.ItemLogic
import org.wagham.kabotapi.utils.DnDSourceList
import org.wagham.kabotapi.utils.authenticatedDelete
import org.wagham.kabotapi.utils.authenticatedGet
import org.wagham.kabotapi.utils.authenticatedPost
import org.wagham.kabotapi.utils.authenticatedPut

fun Routing.itemController() = route("/item") {
    val itemLogic by inject<ItemLogic>()

    authenticatedGet("") {
        val items = itemLogic.getItems(it.guildId).toList()
        call.respond(items)
    }

    authenticatedPost("/search") {
        val limit = call.request.queryParameters["limit"]?.toInt()
        val nextAt = call.request.queryParameters["nextAt"]?.toInt()
        val query = call.request.queryParameters["query"]
        val label = try {
            call.receiveNullable<LabelStub>()
        } catch (_: Exception) { null }
        val paginatedItems = itemLogic.searchItems(
            guildId = it.guildId,
            label = label,
            query = query,
            limit = limit,
            skip = nextAt
        )
        call.respond(paginatedItems)
    }

    authenticatedPost("/byIds") {
        val ids = call.receive<ListOfIdsDto>().ids
        val items = itemLogic.getItems(it.guildId, ids).toList()
        call.respond(items)
    }

    authenticatedGet("/materialsBy/{itemId}") {
        val itemId = checkNotNull(call.parameters["itemId"]) {
            "Item Id must not be null"
        }
        val materials = itemLogic.isMaterialOf(it.guildId, itemId).map { item -> item.name }.toSet()
        call.respond(ListOfIdsDto(materials))
    }

    authenticatedPost("", roles = setOf(NyxRoles.MANAGE_ITEMS)) {
        val itemToCreate = call.receive<Item>()
        itemLogic.createItem(it.guildId, itemToCreate)
        call.respond(StatusResponse(true))
    }

    authenticatedPut("", roles = setOf(NyxRoles.MANAGE_ITEMS)) {
        val itemToUpdate = call.receive<ItemUpdate>()
        itemLogic.updateItem(it.guildId, itemToUpdate.item, itemToUpdate.originalName)
        call.respond(StatusResponse(true))
    }

    authenticatedDelete("/{itemId}", roles = setOf(NyxRoles.DELETE_ITEMS)) {
        val itemId = checkNotNull(call.parameters["itemId"]) {
            "Item Id must not be null"
        }
        itemLogic.deleteItem(it.guildId, itemId)
        call.respond(StatusResponse(true))
    }

    authenticatedGet("/sources") {
        call.respond(DnDSourceList.manuals)
    }


}