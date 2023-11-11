package org.wagham.kabotapi.entities.config

import io.ktor.server.config.*

data class DiscordConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUrl: String,
    val apiEndpoint: String = "https://discord.com/api/v10"
) {
    companion object {
        fun fromConfig(config: ApplicationConfig) = DiscordConfig(
            clientId = config.property("discord.clientId").getString(),
            clientSecret = config.property("discord.clientSecret").getString(),
            redirectUrl = config.property("discord.redirectUrl").getString(),
            apiEndpoint = config.property("discord.apiEndpoint").getString(),
        )
    }
}