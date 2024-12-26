package org.wagham.kabotapi.configuration

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.wagham.kabotapi.controllers.*
import org.wagham.kabotapi.serializers.TimestampDateSerializer
import java.util.Date

fun Application.configureRouting() {
	install(ContentNegotiation) {
		json(Json {
			serializersModule = SerializersModule {
				contextual(Date::class) { TimestampDateSerializer }
			}
		})
	}
	routing {
		authController()
		characterController()
		guildController()
		itemController()
		labelController()
		sessionController()
		playerController()
	}
}