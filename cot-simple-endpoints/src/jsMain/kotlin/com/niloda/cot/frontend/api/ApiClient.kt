package com.niloda.cot.frontend.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    private val baseUrl = "http://localhost:8080/api/cot"
    
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }
    
    suspend fun listTemplates(): Result<List<TemplateResponse>> = try {
        val response = client.get("$baseUrl/templates")
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getTemplate(id: String): Result<TemplateResponse> = try {
        val response = client.get("$baseUrl/templates/$id")
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun createTemplate(request: CreateTemplateRequest): Result<TemplateResponse> = try {
        val response = client.post("$baseUrl/templates") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun deleteTemplate(id: String): Result<Unit> = try {
        client.delete("$baseUrl/templates/$id")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
