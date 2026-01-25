package com.niloda.cot.simple.repository

import com.niloda.cot.domain.Cot
import java.time.Instant

/**
 * Stored representation of a COT including metadata
 */
data class StoredCot(
    val id: String,
    val cot: Cot,
    val dslCode: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
