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

class CotRoutesTestGet {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET api cots id returns COT when it exists`() = testApplication {

        application { module() }

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

        application { module() }

        val response = client.get("/api/cots/nonexistent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }
}
