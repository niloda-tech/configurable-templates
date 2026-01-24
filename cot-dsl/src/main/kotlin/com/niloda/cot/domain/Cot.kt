package com.niloda.cot.domain

import com.niloda.cot.domain.template.Configurable

/**
 * The central domain object: a Configurable Template (Cot).
 * It defines a schema for generating documents or code.
 * @param name The name of the template.
 * @param schema The list of [Configurable] parts that define the structure and rules of the template.
 */
data class Cot(
    val name: String,
    val schema: List<Configurable>
)