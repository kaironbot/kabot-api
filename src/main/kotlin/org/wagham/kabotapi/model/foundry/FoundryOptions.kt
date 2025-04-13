package org.wagham.kabotapi.model.foundry

import kotlinx.serialization.Serializable

@Serializable
data class FoundryOptions(
	val port: Int,
	val upnp: Boolean,
	val fullscreen: Boolean,
	val hostname: String,
	val localHostname: String?,
	val routePrefix: String,
	val sslCert: String?,
	val sslKey: String?,
	val awsConfig: String?,
	val dataPath: String,
	val passwordSalt: String?,
	val proxySSL: Boolean,
	val proxyPort: Int,
	val serviceConfig: String?,
	val updateChannel: String,
	val language: String,
	val upnpLeaseDuration: String?,
	val compressStatic: Boolean,
	val world: String,
	val compressSocket: Boolean,
	val cssTheme: String,
	val deleteNEDB: Boolean,
	val hotReload: Boolean,
	val protocol: String?,
	val telemetry: Boolean,
	val masterName: String? = null
)