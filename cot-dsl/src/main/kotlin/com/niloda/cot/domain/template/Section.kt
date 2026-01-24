package com.niloda.cot.domain.template

/**
 * A container for a list of [Part]s, representing a logical piece of a template.
 */
data class Section(val parts: List<Part>) {

    constructor(part: Part) : this(listOf(part))
    constructor(staticPart: String) : this(listOf(Part.Static(staticPart)))

    /**
     * A sealed interface representing a segment of a template's content.
     * A template is composed of a list of these parts.
     */
    sealed interface Part {
        /**
         * Represents a fixed, static segment of a template.
         * @param content The literal string content.
         */
        data class Static(val content: String) : Part

        /**
         * Represents a dynamic segment of a template that will be replaced by the value of a parameter.
         * @param parameterName The name of the [Parameter] whose value will be injected.
         */
        data class Dynamic(val parameterName: String) : Part
    }

}
