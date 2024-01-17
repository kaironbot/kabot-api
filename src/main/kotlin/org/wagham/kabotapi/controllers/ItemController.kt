package org.wagham.kabotapi.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.koin.ktor.ext.inject
import org.wagham.kabotapi.entities.dto.ListOfIdsDto
import org.wagham.kabotapi.logic.ItemLogic
import org.wagham.kabotapi.utils.authenticatedGet
import org.wagham.kabotapi.utils.authenticatedPost

fun Routing.itemController() = route("/item") {
    val itemLogic by inject<ItemLogic>()

    authenticatedGet("") {
        val items = itemLogic.getItems(it.guildId).toList()
        call.respond(items)
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
}