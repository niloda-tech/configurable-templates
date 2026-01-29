package com.niloda.cot.simple.api

import com.niloda.cot.simple.api.models.*
import com.niloda.cot.simple.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CotRoutesTestErrorHandling {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `POST api cots with invalid DSL returns 400`() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","dslCode":"invalid dsl"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("InvalidDsl", body.error)
    }

    @Test
    fun `GET api cots with non-existent id returns 404`() = testApplication {
        application {
            module()
        }

        val response = client.get("/api/cots/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `PUT api cots with non-existent id returns 404`() = testApplication {
        application {
            module()
        }

        val response = client.put("/api/cots/non-existent-id") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Updated","dslCode":"cot(\"Updated\") {}"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `PUT api cots with invalid DSL returns 400`() = testApplication {
        application {
            module()
        }

        // First create a COT
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"cot(\"TestCot\") { \"test\".text }"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Try to update with invalid DSL
        val response = client.put("/api/cots/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","dslCode":"invalid"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("InvalidDsl", body.error)
    }

    @Test
    fun `DELETE api cots with non-existent id returns 404`() = testApplication {
        application {
            module()
        }

        val response = client.delete("/api/cots/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }

    @Test
    fun `POST api cots generate with non-existent id returns 404`() = testApplication {
        application {
            module()
        }

        val response = client.post("/api/cots/non-existent-id/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{}}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }
}
