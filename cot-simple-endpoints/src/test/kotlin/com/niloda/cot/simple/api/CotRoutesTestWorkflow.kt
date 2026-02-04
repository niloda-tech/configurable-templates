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

class CotRoutesTestWorkflow {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `complete CRUD workflow works correctly`() = testApplication {

        application { module() }

        // 1. List should be empty
        val listResponse1 = client.get("/api/cots")
        val list1 = json.decodeFromString<CotListResponse>(listResponse1.bodyAsText())
        assertEquals(0, list1.cots.size)

        // 2. Create a COT
        val createResponse = client.post("/api/cots") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"MyCot","dslCode":"my code"}""")
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val created = json.decodeFromString<CotDetailResponse>(createResponse.bodyAsText())

        // 3. List should have one COT
        val listResponse2 = client.get("/api/cots")
        val list2 = json.decodeFromString<CotListResponse>(listResponse2.bodyAsText())
        assertEquals(1, list2.cots.size)

        // 4. Get the COT
        val getResponse = client.get("/api/cots/${created.id}")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        // 5. Update the COT
        val updateResponse = client.put("/api/cots/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"UpdatedCot","dslCode":"updated code"}""")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        val updated = json.decodeFromString<CotDetailResponse>(updateResponse.bodyAsText())
        assertEquals("UpdatedCot", updated.name)

        // 6. Delete the COT
        val deleteResponse = client.delete("/api/cots/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // 7. List should be empty again
        val listResponse3 = client.get("/api/cots")
        val list3 = json.decodeFromString<CotListResponse>(listResponse3.bodyAsText())
        assertEquals(0, list3.cots.size)
    }
}
