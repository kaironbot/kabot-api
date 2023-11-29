package org.wagham.kabotapi.configuration

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.components.ExternalGateway
import org.wagham.kabotapi.components.JWTManager
import org.wagham.kabotapi.entities.config.DiscordConfig
import org.wagham.kabotapi.entities.config.JWTConfig
import org.wagham.kabotapi.entities.config.MongoConfig
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.logic.DiscordLogic
import org.wagham.kabotapi.logic.LabelLogic
import org.wagham.kabotapi.logic.SessionLogic
import org.wagham.kabotapi.logic.impl.CharacterLogicImpl
import org.wagham.kabotapi.logic.impl.DiscordLogicImpl
import org.wagham.kabotapi.logic.impl.LabelLogicImpl
import org.wagham.kabotapi.logic.impl.SessionLogicImpl

fun applicationModules(
    config: ApplicationConfig,
    dbConfig: MongoConfig,
    jwtConfig: JWTConfig,
    discordConfig: DiscordConfig
) = module {
    single<JWTManager> { JWTManager(jwtConfig) }
    single<ExternalGateway> { ExternalGateway(config) }
    single<DatabaseComponent> { DatabaseComponent(dbConfig) }
    single<DiscordLogic> { DiscordLogicImpl(get(), discordConfig)}
    single<CharacterLogic> { CharacterLogicImpl(get()) }
    single<LabelLogic> { LabelLogicImpl(get()) }
    single<SessionLogic> { SessionLogicImpl(get(), get()) }
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
        modules(applicationModules(environment.config, dbConfig, jwtConfig, discordConfig))
    }
}