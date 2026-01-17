package com.niloda.cot.domain.dsl.builders

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import com.niloda.cot.domain.dsl.CotDsl
import com.niloda.cot.domain.dsl.builders.SectionBuilder
import com.niloda.cot.domain.template.Configurable
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

@CotDsl
interface CotBuilder {

    val configurables: MutableList<Configurable>
    val unconditional: SectionBuilder

    fun text(content: String) {
        unconditional.static(content)
    }

    fun dynamic(parameterName: String) {
        unconditional.dynamic(parameterName)
    }

    context(_: Raise<DomainError>)
    fun optional(parameterName: String, section: Section): Configurable.IfPresent =
        Configurable.IfPresent(
            parameterName = validateParamName(parameterName),
            section = section
        ).also {
            configurables.add(it)
        }


    context(_: Raise<DomainError>)
    fun conditional(parameterName: String, section: Section): Configurable.Conditional =
        Configurable.Conditional(
            parameterName = validateParamName(parameterName),
            section = section
        ).also {
            configurables.add(it)
        }

    context(_: Raise<DomainError>)
    fun conditional(parameterName: String, buildSection: SectionBuilder.() -> Unit): Configurable.Conditional =
        conditional(parameterName, section { buildSection() })

    context(_: Raise<DomainError>)
    fun repetition(parameterName: String, section: Section): Configurable.Repetition =
        Configurable.Repetition(
            parameterName = validateParamName(parameterName),
            section = section
        ).also {
            configurables.add(it)
        }

    context(_: Raise<DomainError>)
    fun repetition(parameterName: String, sectionBuild: SectionBuilder.() -> Unit): Configurable.Repetition =
        repetition(parameterName, section { sectionBuild() })

    context(_: Raise<DomainError>)
    fun oneOf(parameterName: String, choices: Map<String, Section>): Configurable.OneOf =
        validateChoices(choices).let {
            Configurable.OneOf(parameterName = validateParamName(parameterName), choices = choices)
                .also {
                    configurables.add(it)
                }
        }

    context(raisesDomainError: Raise<DomainError>)
    fun oneOf(parameterName: String, buildChoices: ChoicesBuilder.() -> Unit): Configurable.OneOf =
        oneOf(
            parameterName = parameterName,
            choices = ChoicesBuilder()
                .apply(buildChoices)
                .result()
        )

    fun section(build: SectionBuilder.() -> Unit): Section = SectionBuilder().apply(build).build()

    fun build(): List<Configurable> {
        // if there are unconditional parts, add them as the first configurable
        val built = unconditional.build()
        if (built.parts.isNotEmpty()) {
            configurables.add(0, Configurable.Unconditional(built))
        }
        return configurables.toList()
    }

    context(_: Raise<DomainError>)
    private fun validateParamName(name: String): String = run {
        ensure(name.isNotBlank()) {
            DomainError.InvalidParameterName(name)
        }
        name
    }

    context(_: Raise<DomainError>)
    private fun validateChoices(choices: Map<String, Section>): Unit = run {
        ensure(condition = choices.isNotEmpty()) {
            DomainError.MissingRequired("choices")
        }
        val dups = choices.keys.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        ensure(condition = dups.isEmpty()) {
            DomainError.DuplicateKey(dups.first(), where = "OneOf.choices")
        }
        ensure(condition = choices.keys.all { it.isNotBlank() }) {
            DomainError.InvalidName("OneOf.choiceKey", "blank key")
        }
    }
}