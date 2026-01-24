package com.niloda.cot.domain.template

/**
 * A sealed interface representing a configurable rule that can be applied to a [Section] or set of [Section]s.
 * These rules define the configurable structure of a [com.niloda.cot.domain.Cot].
 */
sealed interface Configurable {
    /**
     * Content that is always included regardless of parameters.
     */
    data class Unconditional(
        val section: Section
    ) : Configurable
    /**
     * A configurable rule for including a [Section] based on the value of a boolean parameter.
     * @param parameterName The name of the boolean [Parameter] that controls inclusion.
     * @param section The [Section] to be conditionally included.
     */
    data class Conditional(
        val parameterName: String,
        val section: Section
    ) : Configurable

    /**
     * A configurable rule for including a [Section] based on the presence of a parameter.
     * @param parameterName The name of the [Parameter] presence that controls inclusion.
     * @param section The [Section] to be conditionally included.
     */
    data class IfPresent(
        val parameterName: String,
        val section: Section
    ) : Configurable

    /**
     * A configurable rule for repeating a [Section] multiple times.
     * The repetition can be controlled by a number parameter (for count) or a list parameter (for iteration).
     * @param parameterName The name of the [Parameter] controlling the repetition.
     * @param section The [Section] to be repeated.
     */
    data class Repetition(
        val parameterName: String,
        val section: Section
    ) : Configurable

    /**
     * A configurable rule for choosing one [Section] to include from a set of named choices.
     * @param parameterName The name of the string or enum [Parameter] used to select the choice.
     * @param choices A map where keys are the possible choice names and values are the corresponding [Section]s.
     */
    data class OneOf(
        val parameterName: String,
        val choices: Map<String, Section>
    ) : Configurable
}