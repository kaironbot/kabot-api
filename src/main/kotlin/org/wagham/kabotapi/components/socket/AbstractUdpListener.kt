package org.wagham.kabotapi.components.socket

import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread
import io.ktor.util.logging.*

abstract class AbstractUdpListener(
	listenPort: Int,
	protected val logger: Logger,
	protected val enableLogging: Boolean
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
						var data = packet.data.sliceArray(0 until packet.length)
						while (data.isNotEmpty()) {
							val idx = data.indexOf('\n'.code.toByte())

							if (idx == -1) {
								buffer += String(data, 0, data.size)
								break
							} else {
								val line = buffer + String(data, 0, idx)
								data = data.sliceArray(idx + 1 until data.size)
								buffer = ""
								if (line.isNotEmpty()) {
									if (enableLogging) {
										logger.info("Received: $line")
									}
									handlePacket(line)
								}
							}
						}
					} catch (e: Exception) {
						logger.error("Cannot receive packet", e)
					}
				}
			}
		}
	}
}