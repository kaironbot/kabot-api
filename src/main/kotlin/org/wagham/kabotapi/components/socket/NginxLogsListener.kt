package org.wagham.kabotapi.components.socket

import io.ktor.util.logging.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NginxLogsListener(
	listenPort: Int,
	enableLogging: Boolean
): AbstractUdpListener(listenPort, KtorSimpleLogger("NginxLogsListener"), enableLogging) {

	private val broadcastChannel = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)

	fun subscribe(): SharedFlow<String> = broadcastChannel.asSharedFlow()

	override fun handlePacket(packet: String) {
		if(broadcastChannel.subscriptionCount.value > 0) {
			broadcastChannel.tryEmit(packet)
		}
	}
}