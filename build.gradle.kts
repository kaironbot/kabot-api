import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "2.3.7"
val kotlinVersion = "1.9.10"
val logbackVersion = "1.4.12"
val koinKtorVersion = "3.5.0"

version = "0.6.0"
plugins {
	id("io.ktor.plugin") version "2.3.4"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
	kotlin("jvm") version "1.9.20"
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

application {
	mainClass.set("org.wagham.kabotapi.KabotApiApplicationKt")
}

group = "org.wagham"

dependencies {
	implementation(project(":kabot-db-connector"))
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.7.3")
	implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-reactor", version = "1.7.3")
	implementation(group = "com.github.ben-manes.caffeine", name = "caffeine", version = "3.1.8")
	implementation(group = "org.apache.tika", name = "tika-core", version = "2.9.1")

	implementation(group = "io.ktor", name = "ktor-server-core-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-cors-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-content-negotiation-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-serialization-jackson-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-call-logging-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-cio-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-serialization-kotlinx-json", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-serialization-jackson", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-client-cio-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-client-content-negotiation-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-auth", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-auth-jwt", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-server-status-pages", version = ktorVersion)

	implementation(group = "io.insert-koin", name = "koin-ktor", version = koinKtorVersion)
	implementation(group = "io.insert-koin", name = "koin-logger-slf4j", version = koinKtorVersion)

	implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)

	implementation(group = "org.mindrot", name = "jbcrypt", version = "0.4")

	implementation(group="org.litote.kmongo", name="kmongo-coroutine", version = "4.7.0")

	testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.10.1")
	testImplementation(group="io.kotest", name="kotest-assertions-core-jvm", version="5.5.3")
	testImplementation(group="io.kotest", name="kotest-framework-engine-jvm", version="5.5.3")
	testImplementation(group = "io.kotest.extensions", name = "kotest-extensions-spring", version = "1.1.3")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "19"
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_19
	targetCompatibility = JavaVersion.VERSION_19
}

tasks.withType<Test> {
	useJUnitPlatform()
}