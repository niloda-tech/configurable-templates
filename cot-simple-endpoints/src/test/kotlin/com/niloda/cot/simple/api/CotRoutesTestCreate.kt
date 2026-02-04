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

class CotRoutesTestCreate {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `POST api cots creates new COT and returns 201`() = testApplication {

        application { module() }

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

        application { module() }

        val response = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","dslCode":"test"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("InvalidDsl", body.error)
        assertTrue(body.message.contains("empty"))
    }
}
