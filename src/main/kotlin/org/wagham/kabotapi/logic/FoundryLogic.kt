package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.SharedFlow
import org.wagham.kabotapi.entities.dto.InstanceInfoDto
import org.wagham.kabotapi.exceptions.NotFoundException

interface FoundryLogic {

	/**
	 * Send a start signal for the Foundry instance with the specified [instanceUrl], if it's not already active.
	 *
	 * @param instanceUrl the url of the instance to active, without the domain.
	 * @return the url of the instance activated, with the full domain.
	 * @throws NotFoundException if no instance exists with that URL.
	 * @throws IllegalAccessException if the instance cannot be started.
	 */
	suspend fun startInstance(instanceUrl: String): String

	/**
	 * Stops a running foundry instance, given its id.
	 *
	 * @param instanceId the id of the foundry instance.
	 */
	suspend fun stopInstance(instanceId: String)

	/**
	 * @return a [SharedFlow] that publishes the current status of all the foundry instances.
	 */
	fun subscribeToInfoChannel(): SharedFlow<Map<String, InstanceInfoDto>>
}