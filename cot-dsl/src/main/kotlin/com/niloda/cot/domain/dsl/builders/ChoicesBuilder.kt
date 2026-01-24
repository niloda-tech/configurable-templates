package com.niloda.cot.domain.dsl.builders

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import com.niloda.cot.domain.dsl.builders.SectionBuilder
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

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