package com.niloda.cot.frontend.api

import kotlinx.serialization.Serializable

@Serializable
data class TemplateResponse(
    val id: String,
    val name: String,
    val description: String?,
    val parameters: List<ParameterResponse>
)

@Serializable
data class ParameterResponse(
    val name: String,
    val type: String,
    val description: String?,
    val required: Boolean,
    val defaultValue: String?
)

@Serializable
data class CreateTemplateRequest(
    val name: String,
    val description: String?,
    val templateString: String
)

@Serializable
data class UpdateTemplateRequest(
    val name: String?,
    val description: String?,
    val templateString: String?
)

@Serializable
data class GenerateRequest(
    val parameters: Map<String, String>
)

@Serializable
data class GenerateResponse(
    val result: String
)
