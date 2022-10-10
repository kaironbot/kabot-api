package org.wagham.kabotapi.configuration

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldNotBe
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
import java.net.http.HttpResponse

@SpringBootTest(
    classes = [KabotApiApplicationTests::class],
    properties = [
        "spring.main.allow-bean-definition-overriding=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityConfigTest(
    @LocalServerPort val port: Int
): StringSpec() {

    val client: HttpClient = HttpClient.newBuilder().build()
    val testGuild: String = System.getenv("TEST_DB_ID")

    init {
        runBlocking {
            testCors(
                client,
                "http://localhost:${port}/api/items",
                testGuild
            )
        }
    }

}

private suspend fun StringSpec.testCors(
    client: HttpClient,
    url: String,
    guildId: String
) {

    "Has CORS headers" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", guildId)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        val varyHeaders = response.headers().map()["vary"]
        varyHeaders shouldNotBe null
        varyHeaders!!.size shouldBeGreaterThan 0
        varyHeaders shouldContain "Origin"
        varyHeaders shouldContain "Access-Control-Request-Method"
        varyHeaders shouldContain "Access-Control-Request-Headers"
    }

}