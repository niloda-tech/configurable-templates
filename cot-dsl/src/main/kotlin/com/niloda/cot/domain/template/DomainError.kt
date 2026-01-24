package com.niloda.cot.domain.template

/**
 * A sealed interface representing potential errors that can occur within the domain.
 * This is the base type for all defined error states.
 */
sealed interface DomainError {
    /** Template or identifier name is invalid (e.g., blank or contains illegal characters). */
    data class InvalidName(val where: String, val reason: String) : DomainError

    /** Duplicate key detected (e.g., duplicate parameter or choice name). */
    data class DuplicateKey(val key: String, val where: String) : DomainError

    /** A required value is missing. */
    data class MissingRequired(val what: String) : DomainError

    /** A provided key was expected to be present in a set of choices but was not. */
    data class InvalidChoiceKey(val key: String) : DomainError

    /** A parameter name is invalid (blank or malformed). */
    data class InvalidParameterName(val name: String) : DomainError

    // --- Generate domain errors ---

    /** A required parameter was not provided in RenderParams. */
    data class MissingParameter(val name: String) : DomainError

    /** The provided parameter value type did not match expectations. */
    data class TypeMismatch(val name: String, val expected: String, val actual: String) : DomainError

    /** An error occurred while attempting to repeat a section. */
    data class RepeatError(val reason: String) : DomainError
}
