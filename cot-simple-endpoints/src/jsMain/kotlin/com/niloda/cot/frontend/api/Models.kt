package com.niloda.cot.frontend.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CotSummary(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CotDetailResponse(
    val id: String,
    val name: String,
    val dslCode: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CotListResponse(
    val cots: List<CotSummary>
)

@Serializable
data class CreateCotRequest(
    val name: String,
    val dslCode: String
)

@Serializable
data class UpdateCotRequest(
    val name: String,
    val dslCode: String
)

@Serializable
data class GenerateRequest(
    val parameters: Map<String, JsonElement>
)

@Serializable
data class GenerateResponse(
    val output: String
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
