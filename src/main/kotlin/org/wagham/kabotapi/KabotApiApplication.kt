package org.wagham.kabotapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(
	scanBasePackages = [
		"org.wagham.kabotapi.components",
		"org.wagham.kabotapi.configuration",
		"org.wagham.kabotapi.controllers",
		"org.wagham.kabotapi.dao",
		"org.wagham.kabotapi.logic",
		"org.wagham.kabotapi.security"
	 ],
	exclude = [
		DataSourceAutoConfiguration::class
	]
)
class KabotApiApplication

fun main(args: Array<String>) {
	runApplication<KabotApiApplication>(*args)
}
