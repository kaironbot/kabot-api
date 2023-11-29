package org.wagham.kabotapi

import io.ktor.server.application.*
import org.wagham.kabotapi.configuration.configureExceptions
import org.wagham.kabotapi.configuration.configureHTTP
import org.wagham.kabotapi.configuration.configureKoin
import org.wagham.kabotapi.configuration.configureRouting

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
	configureHTTP()
	configureKoin()
	configureExceptions()
	configureRouting()
}