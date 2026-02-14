package com.niloda.cot.domain.dsl.builders

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import com.niloda.cot.domain.dsl.builders.SectionBuilder
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

/**
 * A builder for creating a map of choices in a configurable template DSL.
 * This class accumulates key-value pairs where each key is a unique string
 * and each value is a [Section]. It provides validation to prevent blank or
 * duplicate keys, and supports building sections inline using a [SectionBuilder].
 * The resulting map is immutable and can be retrieved via [result].
 */
class ChoicesBuilder {
    private val choices = linkedMapOf<String, Section>()

    context(_: Raise<DomainError>)
    fun choice(key: String, section: Section) {
        // local validation to keep keys sane
        ensure(key.isNotBlank()) { DomainError.InvalidName("OneOf.choiceKey", "blank key") }
        ensure(!choices.containsKey(key)) { DomainError.DuplicateKey(key, where = "OneOf.choices") }
        choices[key] = section
    }

    context(_: Raise<DomainError>)
    fun choice(key: String, build: SectionBuilder.() -> Unit) {
        choice(key, SectionBuilder().apply(build).build())
    }

    fun result(): Map<String, Section> = choices.toMap()
}