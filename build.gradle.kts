import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(connectorLibs.plugins.kotlin.jvm)
	alias(connectorLibs.plugins.kotlin.serialization)
	alias(libs.plugins.ktor)
}

application {
	mainClass.set("org.wagham.kabotapi.KabotApiApplicationKt")
}

group = "org.wagham"
version = "0.6.0"

dependencies {
	implementation(project(":kabot-db-connector"))
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlin.reflect)
	implementation(libs.bundles.ktor.server)
	implementation(libs.bundles.ktor.serialization)
	implementation(libs.bundles.ktor.client)
	implementation(libs.bundles.koin)
	implementation(connectorLibs.kotlinx.coroutines.core)
	implementation(libs.caffeine)
	implementation(libs.tika)
	implementation(libs.kfswatch)
	implementation(libs.logback)
	implementation(libs.jbcrypt)
	implementation(connectorLibs.kmongo)
	implementation(libs.krontab)

	testImplementation(connectorLibs.bundles.kotest)
	testImplementation(libs.jupyter)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
	useJUnitPlatform()
}