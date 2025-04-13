package org.wagham.kabotapi.components.socket

import kotlinx.serialization.json.Json
import org.wagham.kabotapi.model.pm2.Pm2ProcessInfo

sealed class Command <R> (
	val ts: Long,
	private val command: String,
	private val args: List<String>,
) {

	fun toDatagramPayload(): ByteArray = buildString {
		append(ts)
		append(" $command")
		args.forEach {
			append(" $it")
		}
	}.toByteArray()

	abstract fun parseResponse(response: List<String>): R
}

class Pm2ListCommand : Command<List<Pm2ProcessInfo>>(System.currentTimeMillis(), "list", emptyList()) {

	companion object {
		private val json = Json {
			ignoreUnknownKeys = true
		}
	}

	override fun parseResponse(response: List<String>): List<Pm2ProcessInfo> = response.map {
		json.decodeFromString<Pm2ProcessInfo>(it)
	}

}

data class Pm2StartCommand(
	val target: String,
) : Command<String>(System.currentTimeMillis(), "start", listOf(target)) {

	override fun parseResponse(response: List<String>): String {
		check(response.size == 1 && response.first() == "ok") {
			"Unexpected response $response"
		}
		return "ok"
	}

}

data class Pm2StopCommand(
	val target: String,
) : Command<String>(System.currentTimeMillis(), "stop", listOf(target)) {

	override fun parseResponse(response: List<String>): String {
		check(response.size == 1 && response.first() == "ok") {
			"Unexpected response $response"
		}
		return "ok"
	}

}