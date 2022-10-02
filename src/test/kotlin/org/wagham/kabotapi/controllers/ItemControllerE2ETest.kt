package org.wagham.kabotapi.controllers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.wagham.kabotapi.KabotApiApplicationTests
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

    init {
        runBlocking {
            testItemE2E(
                "http://localhost:${port}/api/items",
                client,
                objectMapper
            )
        }
    }

}

private suspend fun StringSpec.testItemE2E(
    url: String,
    client: HttpClient
) {

    "Can get all the Items" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", "248803b5-5880-4e76-b805-4437adbee208")
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 200
        items.size shouldBe 10
    }

}