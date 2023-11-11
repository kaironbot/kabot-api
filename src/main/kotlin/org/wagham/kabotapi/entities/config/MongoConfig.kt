package org.wagham.kabotapi.entities.config

import io.ktor.server.config.*

data class MongoConfig(
    val adminUser: String,
    val adminPwd: String,
    val databaseName: String,
    val databaseIp: String,
    val databasePort: Int
) {
    companion object {
        fun fromConfig(config: ApplicationConfig) = MongoConfig(
            adminUser = config.property("mongodb.adminUser").getString(),
            adminPwd = config.property("mongodb.adminPwd").getString(),
            databaseName = config.property("mongodb.databaseName").getString(),
            databaseIp = config.property("mongodb.databaseIp").getString(),
            databasePort = config.property("mongodb.databasePort").getString().toInt()
        )
    }
}