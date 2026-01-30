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

class CotRoutesTestErrorHandling_DeleteWithNonExistentId {

    private val json = Json { ignoreUnknownKeys = true }

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
}
