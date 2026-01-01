package com.niloda.cot.domain.dsl

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.Param
import com.niloda.cot.Params
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.template.Configurable
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

/**
 * Entry point DSL to build a [Cot] in a typed, validated manner.
 *
 * Usage:
 *
 * val c = cot("Template") {
 *   conditional("enabled") {
 *     section {
 *       static("Hello, ")
 *       dynamic("name")
 *     }
 *   }
 * }
 */
fun cot(name: String, build: CotBuilder.() -> Unit): Either<DomainError, Cot> = either {
    // Basic name validation using Raise DSL helpers
    ensure(name.isNotBlank()) { DomainError.InvalidName("Cot.name", reason = "blank") }
    val builder = CotBuilder(this).apply(build)
    builder.build()
    Cot(name = name, schema = builder.result())
}

class CotBuilder(private val R: Raise<DomainError>) {
    private val configurables = mutableListOf<Configurable>()
    // collects top-level, always-included content
    private val unconditional = SectionBuilder()

    // Allow raw text at top-level via explicit function
    fun text(content: String) {
        unconditional.static(content)
    }

    fun dynamic(parameter: String) {
        unconditional.dynamic(parameter)
    }

    // Allow Kotlin DSL style +"text" at top-level
    operator fun String.unaryPlus() {
        text(this)
    }

    operator fun <T> Enum<T>.unaryMinus(): Section.Part.Dynamic where T : Enum<T>, T: Param<T> =
        Section.Part.Dynamic(name)

    operator fun String.unaryMinus(): Section.Part.Static =
        Section.Part.Static(this)
//    operator fun String.get(param: String): Boolean =
//        conditional

    operator fun String.compareTo(output: Section): Int {
        conditional(this, output)
        return 0
    }

    infix fun Section._if(param: String): Int {
        conditional(param, this)
        return 0
    }

    infix fun Section.Part._if(param: String): Int {
        conditional(param, Section(this))
        return 0
    }


    infix fun Section.Part.ifParam(param: String) {
        conditional(param, Section(this))
    }

    infix fun Section.Part.ifParam(param: Enum<*>) {
        conditional(param.name, Section(this))
    }

    infix operator fun Section.Part.rem(whatever: Param<*>) =
        Section.Part.Dynamic(whatever.toString())


//    operator fun get(param: String): Section.Part.Dynamic =
//        Section.Part.Dynamic(param)


    infix fun _if._then(section: Section) {
        conditional(param, section)
    }

    infix operator fun String.invoke(other: String) = ""

    fun conditional(parameterName: String, section: Section): Configurable.Conditional =
        Configurable.Conditional(parameterName = validateParamName(parameterName), section = section)
            .also { configurables.add(it) }

    fun conditional(parameterName: String, sectionBuild: SectionBuilder.() -> Unit): Configurable.Conditional =
        conditional(parameterName, section { sectionBuild() })

    fun repetition(parameterName: String, section: Section): Configurable.Repetition =
        Configurable.Repetition(parameterName = validateParamName(parameterName), section = section)
            .also { configurables.add(it) }

    fun repetition(parameterName: String, sectionBuild: SectionBuilder.() -> Unit): Configurable.Repetition =
        repetition(parameterName, section { sectionBuild() })

    fun oneOf(parameterName: String, choices: Map<String, Section>): Configurable.OneOf =
        validateChoices(choices).let {
            Configurable.OneOf(parameterName = validateParamName(parameterName), choices = choices)
                .also { configurables.add(it) }
        }

    fun oneOf(parameterName: String, buildChoices: ChoicesBuilder.() -> Unit): Configurable.OneOf =
        oneOf(parameterName, ChoicesBuilder(R).apply(buildChoices).result())

    fun section(build: SectionBuilder.() -> Unit): Section = SectionBuilder().apply(build).build()

    fun build(): Unit {
        // if there are unconditional parts, add them as the first configurable
        val built = unconditional.build()
        if (built.parts.isNotEmpty()) {
            configurables.add(0, Configurable.Unconditional(built))
        }
    }

    fun result(): List<Configurable> = configurables.toList()

    private fun validateParamName(name: String): String = R.run {
        ensure(name.isNotBlank()) { DomainError.InvalidParameterName(name) }
        name
    }

    private fun validateChoices(choices: Map<String, Section>): Unit = R.run {
        ensure(choices.isNotEmpty()) { DomainError.MissingRequired("choices") }
        val dups = choices.keys.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        ensure(dups.isEmpty()) { DomainError.DuplicateKey(dups.first(), where = "OneOf.choices") }
        ensure(choices.keys.all { it.isNotBlank() }) { DomainError.InvalidName("OneOf.choiceKey", "blank key") }
    }
}



class SectionBuilder {
    private val parts = mutableListOf<Section.Part>()

    fun static(content: String) {
        parts.add(Section.Part.Static(content))
    }

    // Allow Kotlin DSL style +"text" inside sections
    operator fun String.unaryPlus() {
        static(this)
    }

    // Allow Kotlin DSL style -"text" to define an inline Static section of text


    // Alias for readability
    fun text(content: String) = static(content)

    fun dynamic(parameterName: String) {
        parts.add(Section.Part.Dynamic(parameterName))
    }

    fun build(): Section = Section(parts.toList())
}

class ChoicesBuilder(private val R: Raise<DomainError>) {
    private val choices = linkedMapOf<String, Section>()

    fun choice(key: String, section: Section) {
        // local validation to keep keys sane
        R.ensure(key.isNotBlank()) { DomainError.InvalidName("OneOf.choiceKey", "blank key") }
        R.ensure(!choices.containsKey(key)) { DomainError.DuplicateKey(key, where = "OneOf.choices") }
        choices[key] = section
    }

    fun choice(key: String, build: SectionBuilder.() -> Unit) {
        choice(key, SectionBuilder().apply(build).build())
    }

    fun result(): Map<String, Section> = choices.toMap()
}

// no context receiver usage to avoid experimental flags

class _if(val param: String)

