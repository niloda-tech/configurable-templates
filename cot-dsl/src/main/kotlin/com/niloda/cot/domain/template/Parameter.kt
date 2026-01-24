package com.niloda.cot.domain.template

/**
 * Represents a parameter in a template schema, with a name, description, and a specific [DataType].
 * This is a core component for defining the data requirements of a [com.niloda.cot.domain.Cot].
 */
data class Parameter(
    val name: String,
    val description: String,
    val type: DataType
)
