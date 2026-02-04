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

class CotRoutesTestErrorHandling_GenerateWithNonExistentId {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `POST api cots generate with non-existent id returns 404`() = testApplication {

        application { module() }

        val response = client.post("/api/cots/non-existent-id/generate") {
            contentType(ContentType.Application.Json)
            setBody("""{"parameters":{}}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val body = json.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("CotNotFound", body.error)
    }
}
