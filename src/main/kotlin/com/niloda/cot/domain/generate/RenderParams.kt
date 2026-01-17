package com.niloda.cot.domain.generate

/**
 * Immutable parameters used during rendering of a Cot.
 */
class RenderParams private constructor(private val values: Map<String, Any?>) {
    fun get(name: String): Any? = values[name]

    fun contains(name: String): Boolean = values.containsKey(name)

    companion object {
        fun of(entries: Map<String, Any?>): RenderParams = RenderParams(entries.toMap())
        fun of(vararg entries: Pair<String, Any?>): RenderParams = RenderParams(entries.toMap())
        fun empty(): RenderParams = RenderParams(emptyMap())
    }
}
