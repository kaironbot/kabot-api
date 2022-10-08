package org.wagham.kabotapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KabotApiApplication

fun main(args: Array<String>) {
	runApplication<KabotApiApplication>(*args)
}
