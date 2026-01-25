package com.niloda.cot.simple.api

import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.simple.api.models.*
import com.niloda.cot.simple.module
import com.niloda.cot.simple.repository.InMemoryCotRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CotRoutesTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET api cots returns empty list initially`() = testApplication {
        application {
            module()
        }

        val response = client.get("/api/cots")
        
        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<CotListResponse>(response.bodyAsText())
        assertTrue(body.cots.isEmpty())
    }

    @Test
    fun `POST api cots creates new COT and returns 201`() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestTemplate","dslCode":"cot(\"TestTemplate\") { \"Hello\".text }"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = json.decodeFromString<CotDetailResponse>(response.bodyAsText())
        assertEquals("TestTemplate", body.name)
        assertNotNull(body.id)
        assertNotNull(body.createdAt)
        assertNotNull(body.updatedAt)
    }

    @Test
    fun `POST api cots with empty name returns 400`() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","dslCode":"test"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("InvalidDsl", body.error)
        assertTrue(body.message.contains("empty"))
    }

    @Test
    fun `GET api cots id returns COT when it exists`() = testApplication {
        application {
            module()
        }

        // Create a COT first
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestTemplate","dslCode":"code"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Get the COT
        val response = client.get("/api/cots/${created.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<CotDetailResponse>(response.bodyAsText())
        assertEquals(created.id, body.id)
        assertEquals("TestTemplate", body.name)
    }

    @Test
    fun `GET api cots id returns 404 when COT does not exist`() = testApplication {
        application {
            module()
        }

        val response = client.get("/api/cots/nonexistent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `PUT api cots id updates COT and returns 200`() = testApplication {
        application {
            module()
        }

        // Create a COT first
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Original","dslCode":"original code"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Update the COT
        val response = client.put("/api/cots/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Updated","dslCode":"updated code"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<CotDetailResponse>(response.bodyAsText())
        assertEquals(created.id, body.id)
        assertEquals("Updated", body.name)
        assertEquals("updated code", body.dslCode)
        assertEquals(created.createdAt, body.createdAt)
        assertNotEquals(created.updatedAt, body.updatedAt)
    }

    @Test
    fun `PUT api cots id returns 404 when COT does not exist`() = testApplication {
        application {
            module()
        }

        val response = client.put("/api/cots/nonexistent-id") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test","dslCode":"code"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `PUT api cots id with empty name returns 400`() = testApplication {
        application {
            module()
        }

        // Create a COT first
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Original","dslCode":"code"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Try to update with empty name
        val response = client.put("/api/cots/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","dslCode":"code"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("InvalidDsl", body.error)
    }

    @Test
    fun `DELETE api cots id removes COT and returns 204`() = testApplication {
        application {
            module()
        }

        // Create a COT first
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestTemplate","dslCode":"code"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Delete the COT
        val deleteResponse = client.delete("/api/cots/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // Verify it's deleted
        val getResponse = client.get("/api/cots/${created.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `DELETE api cots id returns 404 when COT does not exist`() = testApplication {
        application {
            module()
        }

        val response = client.delete("/api/cots/nonexistent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `GET api cots returns all created COTs`() = testApplication {
        application {
            module()
        }

        // Create multiple COTs
        client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Cot1","dslCode":"code1"}""")
        }
        client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Cot2","dslCode":"code2"}""")
        }
        client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Cot3","dslCode":"code3"}""")
        }

        // Get all COTs
        val response = client.get("/api/cots")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<CotListResponse>(response.bodyAsText())
        assertEquals(3, body.cots.size)

        val names = body.cots.map { it.name }.toSet()
        assertTrue(names.contains("Cot1"))
        assertTrue(names.contains("Cot2"))
        assertTrue(names.contains("Cot3"))
    }

    @Test
    fun `complete CRUD workflow works correctly`() = testApplication {
        application {
            module()
        }

        // 1. List should be empty
        val listResponse1 = client.get("/api/cots")
        val list1 = json.decodeFromString<CotListResponse>(listResponse1.bodyAsText())
        assertEquals(0, list1.cots.size)

        // 2. Create a COT
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"MyCot","dslCode":"my code"}""")
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // 3. List should have one COT
        val listResponse2 = client.get("/api/cots")
        val list2 = json.decodeFromString<CotListResponse>(listResponse2.bodyAsText())
        assertEquals(1, list2.cots.size)

        // 4. Get the COT
        val getResponse = client.get("/api/cots/${created.id}")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        // 5. Update the COT
        val updateResponse = client.put("/api/cots/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"UpdatedCot","dslCode":"updated code"}""")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        val updated = json.decodeFromString<CotDetailResponse>(updateResponse.bodyAsText())
        assertEquals("UpdatedCot", updated.name)

        // 6. Delete the COT
        val deleteResponse = client.delete("/api/cots/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // 7. List should be empty again
        val listResponse3 = client.get("/api/cots")
        val list3 = json.decodeFromString<CotListResponse>(listResponse3.bodyAsText())
        assertEquals(0, list3.cots.size)
    }

    @Test
    fun `CotSummary in list response contains correct fields`() = testApplication {
        application {
            module()
        }

        // Create a COT
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"code"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Get the list
        val listResponse = client.get("/api/cots")
        val list = json.decodeFromString<CotListResponse>(listResponse.bodyAsText())

        assertEquals(1, list.cots.size)
        val summary = list.cots[0]
        assertEquals(created.id, summary.id)
        assertEquals("TestCot", summary.name)
        assertEquals(created.createdAt, summary.createdAt)
        assertEquals(created.updatedAt, summary.updatedAt)
    }

    @Test
    fun `POST api cots id generate returns generated output`() = testApplication {
        application {
            module()
        }

        // Create a COT with conditional logic
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"conditional(\"enabled\") { \"Hello\" }"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Generate with enabled=true
        val generateResponse = client.post("/api/cots/${created.id}/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{"enabled":true}}""")
        }

        assertEquals(HttpStatusCode.OK, generateResponse.status)
        val body = json.decodeFromString<GenerateResponse>(generateResponse.bodyAsText())
        assertTrue(body.output.contains("Hello") || body.output.isNotEmpty())
    }

    @Test
    fun `POST api cots id generate with missing parameter returns 400`() = testApplication {
        // We'll use a custom module that we can access the repository from
        val testRepository = InMemoryCotRepository()
        
        // Create a COT directly using DSL
        val cotResult = cot("TestCot") {
            conditional("enabled") {
                static("Hello")
            }
        }
        
        val cotId = cotResult.fold(
            { error("Failed to create COT: $it") },
            { c -> 
                testRepository.create(c, "conditional(\"enabled\") { \"Hello\" }")
                    .fold({ error("Failed to store COT: $it") }, { it.id })
            }
        )
        
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                get("/health") {
                    call.respond(mapOf("status" to "ok"))
                }
                cotRoutes(testRepository)
            }
        }

        // Generate without required parameter
        val generateResponse = client.post("/api/cots/$cotId/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{}}""")
        }

        assertEquals(HttpStatusCode.BadRequest, generateResponse.status)
        val body = json.decodeFromString<ErrorResponse>(generateResponse.bodyAsText())
        assertEquals("GenerationError", body.error)
        assertTrue(body.message.contains("enabled") || body.message.contains("MissingParameter"))
    }

    @Test
    fun `POST api cots id generate with type mismatch returns 400`() = testApplication {
        // Use a custom module with shared repository
        val testRepository = InMemoryCotRepository()
        
        // Create a COT directly using DSL with boolean parameter
        val cotResult = cot("TestCot") {
            conditional("enabled") {
                static("Hello")
            }
        }
        
        val cotId = cotResult.fold(
            { error("Failed to create COT: $it") },
            { c -> 
                testRepository.create(c, "conditional(\"enabled\") { \"Hello\" }")
                    .fold({ error("Failed to store COT: $it") }, { it.id })
            }
        )
        
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                cotRoutes(testRepository)
            }
        }

        // Generate with wrong type (string instead of boolean)
        val generateResponse = client.post("/api/cots/$cotId/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{"enabled":"yes"}}""")
        }

        assertEquals(HttpStatusCode.BadRequest, generateResponse.status)
        val body = json.decodeFromString<ErrorResponse>(generateResponse.bodyAsText())
        assertEquals("GenerationError", body.error)
        assertTrue(body.message.contains("TypeMismatch") || body.message.contains("Boolean"))
    }

    @Test
    fun `POST api cots id generate with integer parameter works`() = testApplication {
        application {
            module()
        }

        // Create a COT with repetition
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"repetition(\"count\") { \"A\" }"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Generate with integer parameter
        val generateResponse = client.post("/api/cots/${created.id}/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{"count":3}}""")
        }

        assertEquals(HttpStatusCode.OK, generateResponse.status)
        val body = json.decodeFromString<GenerateResponse>(generateResponse.bodyAsText())
        assertTrue(body.output.isNotEmpty())
    }

    @Test
    fun `POST api cots id generate with string parameter works`() = testApplication {
        application {
            module()
        }

        // Create a COT with oneOf
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"oneOf(\"choice\") { choice(\"a\") { \"A\" }; choice(\"b\") { \"B\" } }"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Generate with string parameter
        val generateResponse = client.post("/api/cots/${created.id}/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{"choice":"b"}}""")
        }

        assertEquals(HttpStatusCode.OK, generateResponse.status)
        val body = json.decodeFromString<GenerateResponse>(generateResponse.bodyAsText())
        assertTrue(body.output.isNotEmpty())
    }

    @Test
    fun `POST api cots id generate returns 404 when COT does not exist`() = testApplication {
        application {
            module()
        }

        val generateResponse = client.post("/api/cots/nonexistent-id/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{}}""")
        }

        assertEquals(HttpStatusCode.NotFound, generateResponse.status)
        val body = json.decodeFromString<ErrorResponse>(generateResponse.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `POST api cots id generate with complex parameters works`() = testApplication {
        application {
            module()
        }

        // Create a COT with multiple parameter types
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"cot(\"test\") { conditional(\"flag\") { repetition(\"num\") { \"X\" } } }"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Generate with multiple parameters
        val generateResponse = client.post("/api/cots/${created.id}/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{"flag":true,"num":2}}""")
        }

        assertEquals(HttpStatusCode.OK, generateResponse.status)
        val body = json.decodeFromString<GenerateResponse>(generateResponse.bodyAsText())
        assertTrue(body.output.isNotEmpty())
    }
}
