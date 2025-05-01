package org.wagham.kabotapi.entities.dto

import kotlinx.serialization.Serializable

@Serializable
data class InstanceInfoDto(
	val id: String,
	val url: String,
	val status: String,
	val masterName: String,
	val cpu: Double,
	val memory: Long,
	val uptime: Long,
	val diskSize: Long,
)
