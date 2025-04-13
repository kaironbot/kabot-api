package org.wagham.kabotapi.entities.config

import io.ktor.server.config.*

data class SocketConfig(
	val commandSendPort: Int,
	val commandReceivePort: Int,
	val logsReceivePort: Int,
	val nginxLogsEnabled: Boolean,
	val commandLogsEnabled: Boolean
) {
	companion object {
		fun fromConfig(config: ApplicationConfig) = SocketConfig(
			commandSendPort = config.property("socket.commandSendPort").getString().toInt(),
			commandReceivePort = config.property("socket.commandReceivePort").getString().toInt(),
			logsReceivePort = config.property("socket.logsReceivePort").getString().toInt(),
			nginxLogsEnabled = config.property("socket.nginxLogsEnabled").getString().toBoolean(),
			commandLogsEnabled = config.property("socket.commandLogsEnabled").getString().toBoolean(),
		)
	}
}