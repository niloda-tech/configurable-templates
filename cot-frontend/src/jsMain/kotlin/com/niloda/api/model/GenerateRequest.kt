package com.niloda.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GenerateRequest(
    val parameters: Map<String, JsonElement>
)
