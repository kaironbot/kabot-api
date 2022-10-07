package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.wagham.db.models.Item
import org.wagham.kabotapi.KabotApiApplicationTests
import org.wagham.kabotapi.exceptions.ErrorResponsePayload
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

@SpringBootTest(
    classes = [KabotApiApplicationTests::class],
    properties = [
        "spring.main.allow-bean-definition-overriding=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemControllerE2ETest(
    @LocalServerPort val port: Int
): StringSpec() {

    val client: HttpClient = HttpClient.newBuilder().build()
    val objectMapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule( ParameterNamesModule() )

    val testGuild: String = System.getenv("TEST_DB_ID")

    init {
        runBlocking {
            testItemE2E(
                "http://localhost:${port}/api/items",
                client,
                testGuild,
                objectMapper
            )
        }
    }

}

private suspend fun StringSpec.testItemE2E(
    url: String,
    client: HttpClient,
    guild: String,
    objectMapper: ObjectMapper
) {

    "Can get all the Items" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, BodyHandlers.ofString())
        }
        val items = objectMapper.readValue(response.body(), object : TypeReference<List<Item>> (){})
        response.statusCode() shouldBe 200
        items.size shouldBe 10
    }

    "Requesting the Items from a non-existing guild results in 404" {
        val errorGuildId = "I_DO_NOT_EXIST"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", errorGuildId)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 404
        val errorMessage = objectMapper.readValue(response.body(), object : TypeReference<ErrorResponsePayload> (){})
        errorMessage.status shouldNotBe null
        errorMessage.message shouldBe "Invalid Guild ID: $errorGuildId"
    }

}