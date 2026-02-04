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

class CotRoutesTestList {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET api cots returns empty list initially`() = testApplication {

        application { module() }

        val response = client.get("/api/cots")
        
        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<CotListResponse>(response.bodyAsText())
        assertTrue(body.cots.isEmpty())
    }

    @Test
    fun `GET api cots returns all created COTs`() = testApplication {

        application { module() }

        // Create multiple COTs
        client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Cot1","dslCode":"code1"}""")
        }
        client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Cot2","dslCode":"code2"}""")
        }
        client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Cot3","dslCode":"code3"}""")
        }

        // Get all COTs
        val response = client.get("/api/cots")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<CotListResponse>(response.bodyAsText())
        assertEquals(3, body.cots.size)

        val names = body.cots.map { it.name }.toSet()
        assertTrue(names.contains("Cot1"))
        assertTrue(names.contains("Cot2"))
        assertTrue(names.contains("Cot3"))
    }

    @Test
    fun `CotSummary in list response contains correct fields`() = testApplication {

        application { module() }

        // Create a COT
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"TestCot","dslCode":"code"}""")
        }
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // Get the list
        val listResponse = client.get("/api/cots")
        val list = json.decodeFromString<CotListResponse>(listResponse.bodyAsText())

        assertEquals(1, list.cots.size)
        val summary = list.cots[0]
        assertEquals(created.id, summary.id)
        assertEquals("TestCot", summary.name)
        assertEquals(created.createdAt, summary.createdAt)
        assertEquals(created.updatedAt, summary.updatedAt)
    }
}
