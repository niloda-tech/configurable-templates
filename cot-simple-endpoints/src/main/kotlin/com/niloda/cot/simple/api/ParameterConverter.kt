package com.niloda.cot.simple.api

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.template.DomainError
import kotlinx.serialization.json.*

/**
 * Convert a map of JsonElements to RenderParams
 */
fun convertToRenderParams(params: Map<String, JsonElement>): Either<DomainError, RenderParams> {
    return try {
        val converted = params.mapValues { (key, value) ->
            convertJsonElement(key, value)
        }
        RenderParams.of(converted).right()
    } catch (e: Exception) {
        DomainError.InvalidParameterName("Failed to convert parameters: ${e.message}").left()
    }
}

/**
 * Convert a single JsonElement to a compatible type
 */
private fun convertJsonElement(key: String, element: JsonElement): Any {
    return when (element) {
        is JsonPrimitive -> {
            when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.boolean
                element.intOrNull != null -> element.int
                element.longOrNull != null -> {
                    val longValue = element.long
                    // Validate that long fits in int range
                    if (longValue > Int.MAX_VALUE || longValue < Int.MIN_VALUE) {
                        throw IllegalArgumentException("Parameter '$key' value $longValue is outside int range")
                    }
                    longValue.toInt()
                }
                element.doubleOrNull != null -> element.double
                else -> element.content
            }
        }
        is JsonNull -> throw IllegalArgumentException("Parameter '$key' cannot be null")
        is JsonArray -> throw IllegalArgumentException("Parameter '$key' cannot be an array")
        is JsonObject -> throw IllegalArgumentException("Parameter '$key' cannot be an object")
    }
}
