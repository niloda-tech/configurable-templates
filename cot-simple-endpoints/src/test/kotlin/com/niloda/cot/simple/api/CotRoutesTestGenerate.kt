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
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CotRoutesTestGenerate {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `POST api cots id generate returns generated output`() = testApplication {

        application { module() }

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

        application { module() }

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

        application { module() }

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

        application { module() }

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

        application { module() }

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
