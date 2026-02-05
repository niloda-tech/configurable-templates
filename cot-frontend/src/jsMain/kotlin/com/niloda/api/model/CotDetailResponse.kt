package com.niloda.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CotDetailResponse(
    val id: String,
    val name: String,
    val dslCode: String,
    val createdAt: String,
    val updatedAt: String
)
