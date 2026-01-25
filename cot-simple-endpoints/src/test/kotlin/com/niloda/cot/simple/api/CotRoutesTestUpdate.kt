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

class CotRoutesTestUpdate {

    private val json = Json { ignoreUnknownKeys = true }

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
}
