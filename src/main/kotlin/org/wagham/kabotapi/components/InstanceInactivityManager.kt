package org.wagham.kabotapi.components

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import dev.inmo.krontab.doInfinity
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.wagham.kabotapi.components.socket.CommandComponent
import org.wagham.kabotapi.components.socket.NginxLogsListener
import org.wagham.kabotapi.components.socket.Pm2ListCommand
import org.wagham.kabotapi.components.socket.Pm2StopCommand
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class InstanceInactivityManager(
	private val instanceTtl: Duration,
	private val commandComponent: CommandComponent,
	private val nginxLogsListener: NginxLogsListener,
	private val instanceConfigManager: InstanceConfigManager,
	private val excludedInstances: Set<String>,
	private val enableLogging: Boolean,
) {

	private val urlExtractingRegex = Regex(".* \"https://fnd\\.kaironbot\\.net/([^/]+).*")
	private val managerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
	private val logger = KtorSimpleLogger(this.javaClass.simpleName)
	private val instanceActivity = Caffeine.newBuilder()
		.expireAfterWrite(instanceTtl.toJavaDuration())
		.evictionListener { target: String?, _: Long?, _: RemovalCause ->
			if (target != null) {
				if (enableLogging) {
					logger.info("Sending stop signal for $target")
				}
				managerScope.launch {
					commandComponent.sendSocketCommand(Pm2StopCommand(target))
				}
			}
		}.build<String, Long>()

	fun startListening() {
		logger.info("Starting inactivity manager")
		listenForZombies()
		listenForLogs()
	}

	private fun listenForZombies() = managerScope.launch {
		delay(instanceTtl)
		doInfinity("0 0 * * * *") {
			try {
				commandComponent.sendSocketCommand(Pm2ListCommand()).filter {
					it.name !in excludedInstances
				}.forEach {
					if ((System.currentTimeMillis() - it.pm2Env.uptime).milliseconds > 1.hours) {
						val lastActivity = instanceActivity.getIfPresent(it.name)
						if (lastActivity == null) {
							commandComponent.sendSocketCommand(Pm2StopCommand(it.name))
						}
					}
				}
			} catch (e: Exception) {
				logger.error("Error while getting zombies", e)
			}
		}
	}

	private fun listenForLogs() = managerScope.launch {
		try {
			nginxLogsListener.subscribe().collect {
				try {
					val info = getInstanceFromLog(it)
					if (info != null) {
						if (enableLogging) {
							logger.info("Setting activity for: ${info.id}")
						}
						instanceActivity.put(info.id, System.currentTimeMillis())
					}
				} catch (e: Exception) {
					logger.error("Error parsing log $it", e)
				}
			}
		} catch (e: Exception) {
			logger.error("Log stream interrupted", e)
		}
	}



	private fun getInstanceFromLog(log: String): InstanceConfigManager.InstanceInfo? =
		urlExtractingRegex.find(log)?.groupValues?.get(1)?.let {
			instanceConfigManager.getInfoByUrl(it)
		}?.takeIf {
			it.id !in excludedInstances
		}
}