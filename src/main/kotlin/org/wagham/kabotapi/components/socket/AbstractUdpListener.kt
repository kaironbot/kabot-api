package org.wagham.kabotapi.components.socket

import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread
import io.ktor.util.logging.*

abstract class AbstractUdpListener(
	listenPort: Int,
	protected val logger: Logger,
	private val enableLogging: Boolean
) {

	private val receiveSocket = DatagramSocket(listenPort)

	protected abstract fun handlePacket(packet: String)

	fun startListening() {
		logger.info("Starting ${this::class.simpleName}")
		thread(start = true, isDaemon = true) {
			receiveSocket.use { socket ->
				val rcvBuffer = ByteArray(10240)
				var buffer = ""
				while(true) {
					try {
						val packet = DatagramPacket(rcvBuffer, rcvBuffer.size)
						socket.receive(packet)
						val idx = packet.data.indexOf('\n'.code.toByte())
						val received = if (idx == -1) null else buffer + String(packet.data, 0, idx)
						buffer =
							if (idx == -1) buffer + String(packet.data, 0, packet.length)
							else String(packet.data, idx + 1, packet.length - idx - 1)
						if (enableLogging) {
							logger.info("Buffer: $buffer")
						}
						if (received != null) {
							if (enableLogging) {
								logger.info("Received: $received")
							}
							handlePacket(received)
						}
					} catch (e: Exception) {
						logger.error("Cannot receive packet", e)
					}
				}
			}
		}
	}
}