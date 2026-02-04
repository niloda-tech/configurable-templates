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

class CotRoutesTestDelete {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `DELETE api cots id removes COT and returns 204`() = testApplication {

        application { module() }

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

        application { module() }

        val response = client.delete("/api/cots/nonexistent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }
}
