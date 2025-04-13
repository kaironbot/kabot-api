package org.wagham.kabotapi.entities.config

import io.ktor.server.config.*

data class SocketConfig(
	val commandSendPort: Int,
	val commandReceivePort: Int,
	val logsReceivePort: Int
) {
	companion object {
		fun fromConfig(config: ApplicationConfig) = SocketConfig(
			commandSendPort = config.property("socket.commandSendPort").getString().toInt(),
			commandReceivePort = config.property("socket.commandReceivePort").getString().toInt(),
			logsReceivePort = config.property("socket.logsReceivePort").getString().toInt(),
		)
	}
}