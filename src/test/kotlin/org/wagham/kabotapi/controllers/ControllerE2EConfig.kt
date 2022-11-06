package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.wagham.kabotapi.KabotApiApplicationTests
import java.net.http.HttpClient

@SpringBootTest(
    classes = [KabotApiApplicationTests::class],
    properties = [
        "spring.main.allow-bean-definition-overriding=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ControllerE2EConfig(
    @LocalServerPort val port: Int
): StringSpec() {

    val client: HttpClient = HttpClient.newBuilder().build()
    val objectMapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule( ParameterNamesModule() )

    val testGuild: String = System.getenv("TEST_DB_ID")

    init {
        runBlocking {
            backgroundE2ETest(
                "http://localhost:${port}/api/background",
                client,
                testGuild,
                objectMapper
            )

            buildingE2ETest(
                "http://localhost:${port}/api/building",
                client,
                testGuild,
                objectMapper
            )

            characterE2ETest(
                "http://localhost:${port}/api/character",
                client,
                testGuild,
                objectMapper
            )

            featE2ETest(
                "http://localhost:${port}/api/feat",
                client,
                testGuild,
                objectMapper
            )

            itemE2ETest(
                "http://localhost:${port}/api/item",
                client,
                testGuild,
                objectMapper
            )

            raceE2ETest(
                "http://localhost:${port}/api/race",
                client,
                testGuild,
                objectMapper
            )

            spellE2ETest(
                "http://localhost:${port}/api/spell",
                client,
                testGuild,
                objectMapper
            )

            subclassE2ETest(
                "http://localhost:${port}/api/subclass",
                client,
                testGuild,
                objectMapper
            )

            utilityE2ETest(
                "http://localhost:${port}/api/utility",
                client,
                testGuild,
                objectMapper
            )
        }
    }

}