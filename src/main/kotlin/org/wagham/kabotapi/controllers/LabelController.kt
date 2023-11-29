package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject
import org.wagham.db.enums.LabelType
import org.wagham.kabotapi.logic.LabelLogic
import org.wagham.kabotapi.utils.authenticatedGet

fun Routing.labelController() = route("/label") {
    val labelLogic by inject<LabelLogic>()
    val mapper = ObjectMapper()

    authenticatedGet("") {
        val type = call.request.queryParameters["labelType"]?.let { rawType ->
            LabelType.valueOf(rawType)
        }
        val labels = labelLogic.getLabels(it.guildId, type).toList()
        call.respond(mapper.writeValueAsString(labels))
    }
}