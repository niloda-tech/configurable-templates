package com.niloda.cot.domain.template

/**
 * A sealed interface representing the type of data a [Parameter] can hold.
 * This allows for a rich, recursive schema definition, including primitives, collections, and nested objects.
 */
sealed interface DataType {
    /** Represents a string of text. */
    data object String : DataType

    /** Represents a numeric value. */
    data object Number : DataType

    /** Represents a true/false value. */
    data object Boolean : DataType

    /**
     * Represents a value that must be one of a predefined set of strings.
     * @param values The list of allowed string values.
     */
    data class Enum(val values: kotlin.collections.List<kotlin.String>) : DataType

    /**
     * Represents a list of elements, where each element is of a specified [DataType].
     * @param elementType The type of elements in the list.
     */
    data class List(val elementType: DataType) : DataType

    /**
     * Represents a nested object with its own set of properties.
     * @param properties The list of [Parameter]s that define the structure of the object.
     */
    data class Object(val properties: kotlin.collections.List<Parameter>) : DataType
}
