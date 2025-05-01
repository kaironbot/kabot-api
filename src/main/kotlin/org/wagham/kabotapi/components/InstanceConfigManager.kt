package org.wagham.kabotapi.components

import dev.inmo.krontab.doInfinity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import io.github.irgaly.kfswatch.KfsDirectoryWatcher
import io.github.irgaly.kfswatch.KfsEvent
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.wagham.kabotapi.entities.foundry.FoundryOptions
import java.io.File
import java.nio.file.Files

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
	private val urlById = mutableMapOf<String, String>()
	private val sizeByInstanceId = mutableMapOf<String, Long>()

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
		startWatchingInstanceSize()
	}

	fun getConfigByUrl(url: String): InstanceInfo? = instancesByUrl[url]

	fun getConfigById(id: String): InstanceInfo? = urlById[id]?.let {
		getConfigByUrl(it)
	}

	fun getFolderSizeById(id: String): Long? = sizeByInstanceId[id]

	private fun getFolderSize(folder: File): Long = folder.walkTopDown().filter {
		it.isFile && !Files.isSymbolicLink(it.toPath())
	}.sumOf { it.length() }

	private fun computeInstancesSize() {
		File(baseFolder).listFiles().filter {
			it.isDirectory
		}.onEach {
			val size = getFolderSize(it)
			sizeByInstanceId[it.name] = size
		}
	}

	private fun startWatchingInstanceSize() {
		computeInstancesSize()
		scope.launch {
			doInfinity("0 0 * * * *") {
				computeInstancesSize()
			}
		}
	}

	private fun updateInstancesWith(optionsFile: File) {
		val options = Json.decodeFromString<FoundryOptions>(optionsFile.readText())
		val info = InstanceInfo(
			id = options.dataPath.split("/").last(),
			url = options.routePrefix,
			name = options.masterName ?: "unknown",
			domain = options.domain
		)
		urlById[info.id] = info.url
		instancesByUrl[info.url] = info.also {
			logger.info("Updating ${info.url} with $it")
		}
	}

	data class InstanceInfo(
		val id: String,
		val url: String,
		val name: String,
		val domain: String?
	)

}