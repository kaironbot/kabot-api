package org.wagham.kabotapi.data


import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult

class PeekableChannel<T>(
	capacity: Int,
	onBufferOverflow: BufferOverflow,
) {

	private val channel = Channel<T>(capacity, onBufferOverflow)
	private var peeked: T? = null

	fun dropPeeked() {
		peeked = null
	}

	suspend fun peek(): T {
		if (peeked == null) {
			peeked = channel.receive()
		}
		return checkNotNull(peeked) { "Peeked cannot be null" }
	}

	suspend fun receive(): T =
		if (peeked == null) {
			channel.receive()
		} else {
			checkNotNull(peeked) { "Peeked cannot be null" }.also {
				peeked = null
			}
		}

	fun trySend(element: T): ChannelResult<Unit> =
		channel.trySend(element)

}