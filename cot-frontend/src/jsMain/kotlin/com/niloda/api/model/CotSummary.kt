package com.niloda.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CotSummary(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)
