package org.wagham.kabotapi.components.socket

import kotlin.concurrent.thread
import io.ktor.util.logging.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

abstract class AbstractTcpListener(
	listenPort: Int,
	protected val logger: Logger,
	protected val enableLogging: Boolean
) {

	private val receiveSocket = ServerSocket(listenPort)

	protected abstract fun handlePacket(packet: String)

	fun startListening() {
		logger.info("Starting ${this::class.simpleName}")
		thread(start = true, isDaemon = true) {
			try {
				while (true) {
					val clientSocket = receiveSocket.accept()
					clientSocket.use { socket ->
						val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
						var line: String?
						while (reader.readLine().also { line = it } != null) {
							if (enableLogging) {
								logger.info("Received: $line")
							}
							handlePacket(line!!)
						}
					}
				}
			} catch (e: Exception) {
				logger.error("Error in server loop", e)
			}
		}
	}
}