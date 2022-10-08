import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "org.wagham"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://repo.repsy.io/mvn/testadirapa/kabot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
	implementation("org.wagham:kabot-db-connector:0.0.2")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.slf4j:slf4j-api:2.0.3")
	implementation("org.slf4j:slf4j-simple:2.0.3")
	testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.4.2")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.0")
	testImplementation("io.kotest:kotest-framework-engine-jvm:5.5.0")
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
	imageName = "testadirapa/kabot-api:${project.version}"
}