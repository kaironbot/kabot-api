package org.wagham.kabotapi.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.models.Character
import org.wagham.db.pipelines.characters.CharacterWithPlayer
import org.wagham.kabotapi.exceptions.ErrorResponsePayload
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

suspend fun StringSpec.characterE2ETest(
    url: String,
    client: HttpClient,
    guild: String,
    objectMapper: ObjectMapper
) {

    "Can get all the Characters" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        val characters = objectMapper.readValue(response.body(), object : TypeReference<List<Character>>(){})
        response.statusCode() shouldBe 200
        characters.size shouldBeGreaterThan 0
    }

    "Requesting the Characters from a non-existing guild results in 404" {
        val errorGuildId = "I_DO_NOT_EXIST"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", errorGuildId)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 404
        val errorMessage = objectMapper.readValue(response.body(), object : TypeReference<ErrorResponsePayload>(){})
        errorMessage.status shouldNotBe null
        errorMessage.message shouldBe "Invalid Guild ID: $errorGuildId"
    }

    "Requesting the Characters with no Guild ID should result in 400" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 400
    }

    "Should be possible to get the current Character for a player" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 200
        val characters = objectMapper.readValue(response.body(), object : TypeReference<List<Character>>(){})
        characters.size shouldBeGreaterThan 0

        val anActiveCharacter = characters.firstOrNull{ it.status == CharacterStatus.active}
        anActiveCharacter shouldNotBe null

        val activeRequest = HttpRequest.newBuilder()
            .uri(URI.create("$url/active?player=${anActiveCharacter!!.player}"))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val activeResponse = withContext(Dispatchers.IO) {
            client.send(activeRequest, HttpResponse.BodyHandlers.ofString())
        }

        activeResponse.statusCode() shouldBe 200
        val fetchedCharacter = objectMapper.readValue(activeResponse.body(), object : TypeReference<Character>(){})
        fetchedCharacter.name shouldBe anActiveCharacter.name
        fetchedCharacter.player shouldBe anActiveCharacter.player
        fetchedCharacter.characterClass shouldBe anActiveCharacter.characterClass
        fetchedCharacter.race shouldBe anActiveCharacter.race
        fetchedCharacter.status shouldBe CharacterStatus.active
    }

    "Requesting the active character without passing the player should result in 400" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/active"))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val activeResponse = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }

        activeResponse.statusCode() shouldBe 400
    }

    "Requesting the active character without passing the guildId should result in 400" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/active?player=test"))
            .GET()
            .build()

        val activeResponse = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }

        activeResponse.statusCode() shouldBe 400
    }

    "Requesting the active character with a non valid player should result in 404" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/active?player=FAILURE"))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val activeResponse = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }

        activeResponse.statusCode() shouldBe 404
    }

    "Can get all the Characters with Players" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/withPlayer"))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        val characters = objectMapper.readValue(response.body(), object : TypeReference<List<CharacterWithPlayer>>(){})
        response.statusCode() shouldBe 200
        characters.size shouldBeGreaterThan 0
    }

    "Requesting the Characters with Players with no guild id should result in 400" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/withPlayer"))
            .GET()
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 400
    }

    "Requesting the Characters with players from a non-existing guild results in 404" {
        val errorGuildId = "I_DO_NOT_EXIST"
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/withPlayer"))
            .GET()
            .header("Guild-ID", errorGuildId)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 404
        val errorMessage = objectMapper.readValue(response.body(), object : TypeReference<ErrorResponsePayload>(){})
        errorMessage.status shouldNotBe null
        errorMessage.message shouldBe "Invalid Guild ID: $errorGuildId"
    }

    "Can get all the Characters with Players specifying a status" {
        val status = CharacterStatus.retired
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/withPlayer?status=$status"))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        val characters = objectMapper.readValue(response.body(), object : TypeReference<List<CharacterWithPlayer>>(){})
        response.statusCode() shouldBe 200
        characters.size shouldBeGreaterThan 0
        characters.onEach {
            it.status shouldBe status
        }
    }

    "Requesting the Characters with players with a non-existing status results in 400" {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url/withPlayer?status=INVALID"))
            .GET()
            .header("Guild-ID", guild)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        response.statusCode() shouldBe 400
    }

}