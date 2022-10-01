package org.wagham.kabotapi.controllers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.wagham.kabotapi.services.DatabaseService

@RestController
@RequestMapping("/api/items")
class ItemController (
    val database: DatabaseService
) {

    

}