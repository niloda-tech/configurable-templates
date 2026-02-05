package com.niloda.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateCotRequest(
    val name: String,
    val dslCode: String
)
