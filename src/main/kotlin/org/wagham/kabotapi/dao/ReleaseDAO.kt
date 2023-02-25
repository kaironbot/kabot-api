package org.wagham.kabotapi.dao

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import mu.KotlinLogging
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.wagham.kabotapi.entities.Release
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.Path

@Service
class ReleaseDAO(
    private val objectMapper: ObjectMapper
) {

    private val releaseDir = "module/release"
    private val httpClient = HttpClient(CIO)
    private val githubToken = System.getenv("RELEASE_GITHUB_TOKEN")!!
    private val log = KotlinLogging.logger {}

    @InternalAPI
    suspend fun downloadLatestRelease(releaseURL: String) {
        releaseDir.also {
            if(!File(it).exists()) File(it).mkdirs()
        }
        val releaseRaw = httpClient.get(releaseURL) {
            header("Authorization",  "Bearer $githubToken")
        }.body<String>()

        val release = objectMapper.readValue<Release>(releaseRaw)

        File(releaseDir).listFiles()
            ?.firstOrNull()
            ?.takeIf { it.name != release.version }
            ?.deleteRecursively()
        Path(releaseDir, release.version).toFile().also { newFolder -> newFolder.mkdir() }
        release.assets.forEach { asset ->
            val outputFile = Path(releaseDir, release.version, asset.name).toFile()
            httpClient.get(asset.url) {
                header("Authorization",  "Bearer $githubToken")
                header("Accept",  "application/octet-stream")
            }.content.copyAndClose(outputFile.writeChannel()).also { wroteBytes ->
                if(wroteBytes == 0L) throw IllegalStateException("Error in downloading ${asset.name}")
            }
        }
        log.info { "Successfully download module version ${release.version}" }
    }

    fun manifestAsResource(): Resource {
        val manifest = File(releaseDir).listFiles()
            ?.firstOrNull()
            ?.listFiles()
            ?.firstOrNull { it.name.startsWith("module") }
            ?: throw FileNotFoundException("Manifest not found!")
        val resource = UrlResource(manifest.toURI())
        return if (resource.exists() || resource.isReadable)
            resource
        else
            throw FileNotFoundException("Manifest not found or unreadable!")
    }

    fun moduleAsResource(): Resource {
        val manifest = File(releaseDir).listFiles()
            ?.firstOrNull()
            ?.listFiles()
            ?.firstOrNull { it.name.startsWith("Release") }
            ?: throw FileNotFoundException("Release not found!")
        val resource = UrlResource(manifest.toURI())
        return if (resource.exists() || resource.isReadable)
            resource
        else
            throw FileNotFoundException("Release not found or unreadable!")
    }

}