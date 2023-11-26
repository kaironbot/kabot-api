package org.wagham.kabotapi.configuration

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.wagham.kabotapi.controllers.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        authController()
        characterController()
        labelController()
        guildController()
        sessionController()
    }
}