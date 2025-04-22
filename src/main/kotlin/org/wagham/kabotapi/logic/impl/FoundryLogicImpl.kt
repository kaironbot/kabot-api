package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wagham.kabotapi.components.InstanceConfigManager
import org.wagham.kabotapi.components.InstanceInactivityManager
import org.wagham.kabotapi.components.socket.CommandComponent
import org.wagham.kabotapi.components.socket.Pm2ListCommand
import org.wagham.kabotapi.components.socket.Pm2StartCommand
import org.wagham.kabotapi.entities.dto.InstanceInfoDto
import org.wagham.kabotapi.exceptions.NotFoundException
import org.wagham.kabotapi.logic.FoundryLogic
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.minutes

class FoundryLogicImpl(
	private val defaultDomain: String,
	private val instanceConfigManager: InstanceConfigManager,
	private val instanceInactivityManager: InstanceInactivityManager,
	private val commandComponent: CommandComponent,
	private val excludedInstances: Set<String>,
) : FoundryLogic {

	private val logicScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
	private val publishingJob = AtomicReference<Job?>(null)
	private val infoChannel = MutableStateFlow<Map<String, InstanceInfoDto>>(emptyMap())

	override fun subscribeToInfoChannel(): SharedFlow<Map<String, InstanceInfoDto>> {
		val currentJob = publishingJob.get()
		if (currentJob == null || currentJob.isCompleted) {
			val newJob = logicScope.launch(start = CoroutineStart.LAZY) {
				do {
					val newState = commandComponent
						.sendSocketCommand(Pm2ListCommand())
						.associateTo(HashMap()) { info ->
							val config = instanceConfigManager.getConfigById(info.name)
							val infoDto = InstanceInfoDto(
								id = info.name,
								url = config?.fullUrl ?: defaultDomain,
								masterName = config?.name ?: "unknown",
								status = info.pm2Env.status,
								cpu = info.monit.cpu,
								memory = info.monit.memory,
								uptime = info.pm2Env.uptime,
								diskSize = instanceConfigManager.getFolderSizeById(info.name) ?: 0
							)
							info.name to infoDto
					}
					infoChannel.emit(newState)
					delay(1.minutes)
				} while (isActive && infoChannel.subscriptionCount.value > 0)
			}
			if (publishingJob.compareAndSet(currentJob, newJob)) {
				newJob.start()
			} else {
				newJob.cancel()
			}
		}
		return infoChannel.asSharedFlow()
	}

	override suspend fun startInstance(instanceUrl: String): String {
		val instanceInfo = instanceConfigManager.getConfigByUrl(instanceUrl)
			?: throw NotFoundException("Instance not found: $instanceUrl")
		if (instanceInfo.id in excludedInstances) {
			throw IllegalAccessException("You cannot start this instance")
		}
		if (instanceInactivityManager.getLastActivityOf(instanceInfo.id) == null) {
			commandComponent.sendSocketCommand(Pm2StartCommand(instanceInfo.id))
		}
		return instanceInfo.fullUrl
	}

	override suspend fun stopInstance(instanceId: String) {
		if (instanceInactivityManager.getLastActivityOf(instanceId) != null) {
			commandComponent.sendSocketCommand(Pm2StartCommand(instanceId))
		}
	}

	private val InstanceConfigManager.InstanceInfo.fullUrl: String
		get() = "${domain ?: defaultDomain}/${url}"
}