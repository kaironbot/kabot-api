package org.wagham.kabotapi.components.socket

import io.ktor.util.logging.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import org.wagham.kabotapi.data.PeekableChannel
import java.net.InetAddress
import java.net.Socket
import kotlin.time.Duration.Companion.seconds

class CommandComponent(
	private val sendPort: Int,
	receivePort: Int,
	enableLogging: Boolean,
): AbstractTcpListener(receivePort, KtorSimpleLogger("CommandComponent"), enableLogging) {

	private val packetChannel = PeekableChannel<ParsedPacket>(capacity = 1000, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val address = InetAddress.getByName("127.0.0.1")
	private val socketMutex = Mutex()

	override fun handlePacket(packet: String) {
		val parts = packet.trim('\n').split('|', limit = 4)
		packetChannel.trySend(
			ParsedPacket(
				parts[0].toLong(),
				parts[1].toInt(),
				parts[2].toInt(),
				parts[3]
			)
		)
	}

	private fun sendCommand(data: ByteArray) {
		Socket(address, sendPort).use { socket ->
			val output = socket.getOutputStream()
			output.write(data)
			output.flush()
		}
	}

	suspend fun <T> sendSocketCommand(command: Command<T>): T = coroutineScope {
		val responseDatagrams = mutableListOf<String>()
		socketMutex.withLock {
			val responseJob = launch {
				do {
					val hasNext = runCatching {
						withTimeout(1.seconds) {
							val next = packetChannel.peek()
							when {
								next.ts < command.ts -> {
									packetChannel.dropPeeked()
									true
								}
								next.ts > command.ts -> false
								else -> {
									responseDatagrams.add(next.msg)
									packetChannel.dropPeeked()
									next.part < next.total
								}
							}
						}
					}.onFailure {
						if (enableLogging) {
							if(it is TimeoutCancellationException) {
								logger.error("Timed out waiting for command $command")
							} else {
								logger.error("Error while waiting for command $command", it)
							}
						}
					}.getOrDefault(false)
				} while (hasNext)
			}
			sendCommand(command.toDatagramPayload())
			responseJob.join()
		}
		command.parseResponse(responseDatagrams)
	}

	private data class ParsedPacket(
		val ts: Long,
		val part: Int,
		val total: Int,
		val msg: String,
	)

}