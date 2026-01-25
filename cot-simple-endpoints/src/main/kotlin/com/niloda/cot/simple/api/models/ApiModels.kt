package com.niloda.cot.simple.api.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Request to create a new COT
 */
@Serializable
data class CreateCotRequest(
    val name: String,
    val dslCode: String
)

/**
 * Request to update an existing COT
 */
@Serializable
data class UpdateCotRequest(
    val name: String,
    val dslCode: String
)

/**
 * Summary of a COT for list view
 */
@Serializable
data class CotSummary(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Detailed response for a single COT
 */
@Serializable
data class CotDetailResponse(
    val id: String,
    val name: String,
    val dslCode: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Response for listing COTs
 */
@Serializable
data class CotListResponse(
    val cots: List<CotSummary>
)

/**
 * Request to generate output from a COT
 */
@Serializable
data class GenerateRequest(
    val parameters: Map<String, JsonElement>
)

/**
 * Response from generating output
 */
@Serializable
data class GenerateResponse(
    val output: String
)

/**
 * Error response model
 */
@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
