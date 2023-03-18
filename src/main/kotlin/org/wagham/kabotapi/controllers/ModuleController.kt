package org.wagham.kabotapi.controllers

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.reactor.mono
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.wagham.kabotapi.dao.ReleaseDAO
import org.wagham.kabotapi.entities.WebhookPayload

@RestController
@RequestMapping("/api/module")
class ModuleController(
    val releaseDAO: ReleaseDAO
) {

    @InternalAPI
    @PostMapping("/update")
    fun updateRelease(
        @RequestBody payload: WebhookPayload
    ) = mono {
        if(payload.action == "released") releaseDAO.downloadLatestRelease(payload.release.url)
    }

    @GetMapping("/download/plutonium")
    fun downloadPlutoniumModule(): ResponseEntity<Resource> {
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wagham_compendium.json")
            .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8")
            .body(releaseDAO.plutoniumModuleAsResource())
    }

    @GetMapping("/download/manifest")
    fun downloadManifest(): ResponseEntity<Resource> {
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=module.json")
            .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8")
            .body(releaseDAO.manifestAsResource())
    }

    @GetMapping("/download/module")
    fun downloadModule(): ResponseEntity<Resource> {
        val moduleResource = releaseDAO.moduleAsResource()
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${moduleResource.filename}")
            .body(moduleResource)
    }

}