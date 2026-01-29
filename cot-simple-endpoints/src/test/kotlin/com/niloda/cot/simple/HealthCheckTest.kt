package com.niloda.cot.simple

import com.niloda.cot.simple.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HealthCheckTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET health returns ok status`() = testApplication {
        application {
            module()
        }

        val response = client.get("/health")
        
        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<HealthResponse>(response.bodyAsText())
        assertEquals("ok", body.status)
    }
}
