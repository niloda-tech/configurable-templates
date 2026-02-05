package com.niloda.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateResponse(
    val output: String
)
