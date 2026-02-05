package com.niloda.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCotRequest(
    val name: String,
    val dslCode: String
)
