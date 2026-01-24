package com.niloda.cot.domain.dsl.builders

import com.niloda.cot.domain.template.Section

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