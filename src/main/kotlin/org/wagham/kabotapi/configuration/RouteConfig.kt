package org.wagham.kabotapi.configuration

import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.wagham.kabotapi.controllers.authController
import org.wagham.kabotapi.controllers.characterController
import org.wagham.kabotapi.controllers.guildController

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        authController()
        characterController()
        guildController()
    }
}