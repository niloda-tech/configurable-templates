package com.niloda.cot.frontend.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    private val baseUrl = "http://localhost:8080/api/cots"
    
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }
    
    suspend fun listCots(): Result<CotListResponse> = try {
        val response = client.get(baseUrl)
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getCot(id: String): Result<CotDetailResponse> = try {
        val response = client.get("$baseUrl/$id")
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun createCot(request: CreateCotRequest): Result<CotDetailResponse> = try {
        val response = client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun updateCot(id: String, request: UpdateCotRequest): Result<CotDetailResponse> = try {
        val response = client.put("$baseUrl/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun deleteCot(id: String): Result<Unit> = try {
        client.delete("$baseUrl/$id")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun generateOutput(id: String, request: GenerateRequest): Result<GenerateResponse> = try {
        val response = client.post("$baseUrl/$id/generate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        Result.success(response.body())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
