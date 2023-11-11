package org.wagham.kabotapi.configuration

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.components.JWTManager
import org.wagham.kabotapi.dao.CharacterDao
import org.wagham.kabotapi.dao.impl.CharacterDaoImpl
import org.wagham.kabotapi.entities.config.DiscordConfig
import org.wagham.kabotapi.entities.config.JWTConfig
import org.wagham.kabotapi.entities.config.MongoConfig
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.logic.DiscordLogic
import org.wagham.kabotapi.logic.impl.CharacterLogicImpl
import org.wagham.kabotapi.logic.impl.DiscordLogicImpl

fun applicationModules(
    dbConfig: MongoConfig,
    jwtConfig: JWTConfig,
    discordConfig: DiscordConfig
) = module {
    single<JWTManager> { JWTManager(jwtConfig) }
    single<DatabaseComponent> { DatabaseComponent(dbConfig) }
    single<CharacterDao> { CharacterDaoImpl(get()) }
    single<DiscordLogic> { DiscordLogicImpl(get(), discordConfig)}
    single<CharacterLogic> { CharacterLogicImpl(get()) }
}

/**
 * Loads into the server all the logic classes.
 *
 * @receiver a ktor [Application]
 */
fun Application.configureKoin() {

    val dbConfig = MongoConfig.fromConfig(environment.config)
    val jwtConfig = JWTConfig.fromConfig(environment.config)
    val discordConfig = DiscordConfig.fromConfig(environment.config)

    install(Koin) {
        slf4jLogger()
        modules(applicationModules(dbConfig, jwtConfig, discordConfig))
    }
}