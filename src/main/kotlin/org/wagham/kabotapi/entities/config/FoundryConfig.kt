package org.wagham.kabotapi.entities.config

import io.ktor.server.config.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class FoundryConfig(
	val instanceFolder: String,
	val instanceTtl: Duration,
	val excludedInstances: Set<String>,
	val enableLogging: Boolean,
) {
	companion object {
		fun fromConfig(config: ApplicationConfig) = FoundryConfig(
			instanceFolder = config.property("foundry.instanceFolder").getString(),
			enableLogging = config.property("foundry.enableLogging").getString().toBoolean(),
			instanceTtl = config.property("foundry.instanceTtlInMinutes").getString().toInt().minutes,
			excludedInstances = config.property("foundry.excludedInstances").getString()
				.split(",")
				.map { it.trim() }
				.filter { it.isNotBlank() }
				.toSet()
		)
	}
}