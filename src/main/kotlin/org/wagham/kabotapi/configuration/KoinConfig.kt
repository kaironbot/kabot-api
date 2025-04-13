package org.wagham.kabotapi.configuration

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.components.ExternalGateway
import org.wagham.kabotapi.components.InstanceConfigManager
import org.wagham.kabotapi.components.InstanceInactivityManager
import org.wagham.kabotapi.components.JWTManager
import org.wagham.kabotapi.components.socket.CommandComponent
import org.wagham.kabotapi.components.socket.NginxLogsListener
import org.wagham.kabotapi.entities.config.DiscordConfig
import org.wagham.kabotapi.entities.config.FoundryConfig
import org.wagham.kabotapi.entities.config.JWTConfig
import org.wagham.kabotapi.entities.config.MongoConfig
import org.wagham.kabotapi.entities.config.SocketConfig
import org.wagham.kabotapi.logic.CharacterLogic
import org.wagham.kabotapi.logic.DiscordLogic
import org.wagham.kabotapi.logic.ItemLogic
import org.wagham.kabotapi.logic.LabelLogic
import org.wagham.kabotapi.logic.PlayerLogic
import org.wagham.kabotapi.logic.SessionLogic
import org.wagham.kabotapi.logic.UtilitiesLogic
import org.wagham.kabotapi.logic.impl.CharacterLogicImpl
import org.wagham.kabotapi.logic.impl.DiscordLogicImpl
import org.wagham.kabotapi.logic.impl.ItemLogicImpl
import org.wagham.kabotapi.logic.impl.LabelLogicImpl
import org.wagham.kabotapi.logic.impl.PlayerLogicImpl
import org.wagham.kabotapi.logic.impl.SessionLogicImpl
import org.wagham.kabotapi.logic.impl.UtilitiesLogicImpl

fun applicationModules(
	config: ApplicationConfig,
	dbConfig: MongoConfig,
	jwtConfig: JWTConfig,
	discordConfig: DiscordConfig,
	socketConfig: SocketConfig,
	foundryConfig: FoundryConfig
) = module {
	single<JWTManager> { JWTManager(jwtConfig) }
	single<ExternalGateway> { ExternalGateway(config) }
	single<DatabaseComponent> { DatabaseComponent(dbConfig) }
	single<CommandComponent> {
		CommandComponent(
			sendPort = socketConfig.commandSendPort,
			receivePort = socketConfig.commandReceivePort,
			enableLogging = socketConfig.commandLogsEnabled
		)
	}
	single<NginxLogsListener> { NginxLogsListener(socketConfig.logsReceivePort, socketConfig.nginxLogsEnabled) }
	single<InstanceConfigManager> { InstanceConfigManager(foundryConfig.instanceFolder) }
	single<InstanceInactivityManager> {
		InstanceInactivityManager(
			instanceTtl = foundryConfig.instanceTtl,
			commandComponent = get<CommandComponent>(),
			nginxLogsListener = get<NginxLogsListener>(),
			instanceConfigManager = get<InstanceConfigManager>(),
			excludedInstances = foundryConfig.excludedInstances,
			enableLogging = foundryConfig.enableLogging,
		)
	}

	single<DiscordLogic> { DiscordLogicImpl(get(), discordConfig)}
	single<CharacterLogic> { CharacterLogicImpl(get(), get()) }
	single<LabelLogic> { LabelLogicImpl(get()) }
	single<SessionLogic> { SessionLogicImpl(get(), get()) }
	single<ItemLogic> { ItemLogicImpl(get()) }
	single<PlayerLogic> { PlayerLogicImpl(get()) }
	single<UtilitiesLogic> { UtilitiesLogicImpl(get()) }
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
	val socketConfig = SocketConfig.fromConfig(environment.config)
	val foundryConfig = FoundryConfig.fromConfig(environment.config)

	install(Koin) {
		slf4jLogger()
		modules(applicationModules(environment.config, dbConfig, jwtConfig, discordConfig, socketConfig, foundryConfig))
	}

	val commandComponent: CommandComponent by inject()
	commandComponent.startListening()
	val nginxLogsListener: NginxLogsListener by inject()
	nginxLogsListener.startListening()
	val instanceConfigManager: InstanceConfigManager by inject()
	instanceConfigManager.startWatching()
	val instanceInactivityManager: InstanceInactivityManager by inject()
	instanceInactivityManager.startListening()
}