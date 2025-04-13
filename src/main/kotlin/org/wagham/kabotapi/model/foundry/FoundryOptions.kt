package org.wagham.kabotapi.model.foundry

import kotlinx.serialization.Serializable

@Serializable
data class FoundryOptions(
	val port: Int,
	val upnp: Boolean = false,
	val fullscreen: Boolean = false,
	val hostname: String? = null,
	val localHostname: String? = null,
	val routePrefix: String,
	val sslCert: String? = null,
	val sslKey: String? = null,
	val awsConfig: String? = null,
	val dataPath: String,
	val passwordSalt: String? = null,
	val proxySSL: Boolean = false,
	val proxyPort: Int? = null,
	val serviceConfig: String? = null,
	val updateChannel: String? = null,
	val language: String? = null,
	val upnpLeaseDuration: String? = null,
	val compressStatic: Boolean = false,
	val world: String? = null,
	val compressSocket: Boolean = false,
	val cssTheme: String? = null,
	val deleteNEDB: Boolean = false,
	val hotReload: Boolean = false,
	val protocol: String? = null,
	val telemetry: Boolean = false,
	val masterName: String? = null
)