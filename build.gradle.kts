import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "2.3.4"

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.8.0"
	kotlin("plugin.spring") version "1.8.0"
}

buildscript {
	repositories {
		mavenCentral() // or gradlePluginPortal()
	}
	dependencies {
		classpath("com.dipien:semantic-version-gradle-plugin:1.3.0")
	}
}

group = "org.wagham"
version = "0.6.0"
java.sourceCompatibility = JavaVersion.VERSION_17

apply(plugin = "com.dipien.semantic-version")

repositories {
	mavenCentral()
	gradlePluginPortal()
	maven { url = uri("https://repo.repsy.io/mvn/testadirapa/kabot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation(group="org.jetbrains.kotlinx", name="kotlinx-coroutines-core", version="1.6.4")
	implementation(group="org.jetbrains.kotlinx", name="kotlinx-coroutines-reactor", version="1.6.4")
	implementation(group="org.wagham", name="kabot-db-connector", version="0.18.8")
	implementation(group="com.fasterxml.jackson.module", name="jackson-module-kotlin", version="2.13.4")
	implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-client-cio", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-serialization-kotlinx-json", version = ktorVersion)
	implementation(group = "io.ktor", name = "ktor-client-content-negotiation", version = ktorVersion)
	implementation(group = "io.github.microutils", name = "kotlin-logging-jvm", version = "2.0.11")
	implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.3")
	implementation(group = "org.slf4j", name = "slf4j-simple", version = "2.0.3")
	implementation(group = "io.jsonwebtoken", name = "jjwt-api", version = "0.11.5")
	implementation(group = "io.jsonwebtoken", name = "jjwt-impl", version = "0.11.5")
	implementation(group = "io.jsonwebtoken", name = "jjwt-jackson", version = "0.11.5")
	implementation("io.ktor:ktor-client-cio-jvm:2.3.4")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.4.2")
	testImplementation(group="io.kotest", name="kotest-assertions-core-jvm", version="5.5.3")
	testImplementation(group="io.kotest", name="kotest-framework-engine-jvm", version="5.5.3")
	testImplementation(group = "io.kotest.extensions", name = "kotest-extensions-spring", version = "1.1.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	imageName = "testadirapa/kabot-api:${version.toString().replace("-SNAPSHOT", "")}"
}

tasks.register("printLibVersion") {
	println(version)
}
