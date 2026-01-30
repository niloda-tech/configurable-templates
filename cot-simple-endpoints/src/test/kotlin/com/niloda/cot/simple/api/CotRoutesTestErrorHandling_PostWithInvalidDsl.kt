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

class CotRoutesTestErrorHandling_PostWithInvalidDsl {

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
}
