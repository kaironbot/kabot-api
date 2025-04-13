package org.wagham.kabotapi.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import io.github.irgaly.kfswatch.KfsDirectoryWatcher
import io.github.irgaly.kfswatch.KfsEvent
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.wagham.kabotapi.model.foundry.FoundryOptions
import java.io.File

class InstanceConfigManager(
	private val baseFolder: String
) {

	companion object {
		private const val OPTIONS_FILENAME = "options.json"
	}

	private val logger = KtorSimpleLogger(this.javaClass.simpleName)
	private val scope = CoroutineScope(Dispatchers.IO)
	private val watcher = KfsDirectoryWatcher(scope)
	private val instancesByUrl = mutableMapOf<String, InstanceInfo>()

	fun startWatching() {
		logger.info("Starting instance manager")
		val dirsToWatch = File(baseFolder).walkTopDown().filter {
			it.isFile && it.name == OPTIONS_FILENAME
		}.map {
			updateInstancesWith(it)
			it.parent
		}.toList()
		scope.launch {
			watcher.add(*dirsToWatch.toTypedArray())
			watcher.onEventFlow.collect {
				try {
					val file = File("${it.targetDirectory}/${it.path}")
					if (file.isFile && file.name == OPTIONS_FILENAME) {
						when (it.event) {
							KfsEvent.Create, KfsEvent.Modify -> {
								updateInstancesWith(file)
							}
							else -> {
								logger.info("Deleted ${file.absolutePath}")
							}
						}
					}
				} catch (e: Exception) {
					logger.error(e.message)
				}
			}
		}
	}

	fun getInfoByUrl(url: String): InstanceInfo? = instancesByUrl[url]

	private fun updateInstancesWith(optionsFile: File) {
		val options = Json.decodeFromString<FoundryOptions>(optionsFile.readText())
		val info = InstanceInfo(
			id = options.dataPath.split("/").last(),
			url = options.routePrefix,
			name = options.masterName ?: "unknown",
		)
		instancesByUrl[info.url] = info.also {
			logger.info("Updating ${info.url} with $it")
		}
	}

	data class InstanceInfo(
		val id: String,
		val url: String,
		val name: String
	)

}