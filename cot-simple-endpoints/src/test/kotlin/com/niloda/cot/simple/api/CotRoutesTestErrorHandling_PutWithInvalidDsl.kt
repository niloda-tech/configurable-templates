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

class CotRoutesTestErrorHandling_PutWithInvalidDsl {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `PUT api cots with invalid DSL returns 400`() = testApplication {

        application { module() }

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
}
