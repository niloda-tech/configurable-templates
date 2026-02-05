package com.niloda.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CotListResponse(
    val cots: List<CotSummary>
)
