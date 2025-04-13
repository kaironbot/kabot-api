package org.wagham.kabotapi.entities.pm2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pm2ProcessInfo(
	val pid: Int,
	@SerialName("pm_id") val pm2Id: Int,
	val name: String,
	@SerialName("pm2_env") val pm2Env: Pm2Env,
	val monit: Pm2Monit
)

@Serializable
data class Pm2Env(
	@SerialName("unstable_restarts") val unstableRestarts: Int,
	@SerialName("pm_uptime") val uptime: Long
)

@Serializable
data class Pm2Monit(
	val memory: Long,
	val cpu: Double,
)