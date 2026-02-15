package com.niloda.cot.sandbox.service.sandbox

import com.niloda.cot.sandbox.domain.SandboxRequest

/**
 * Builds the Kotlin script content with imports and execution logic.
 */
fun buildScriptContent(request: SandboxRequest): String {
    val parametersMap = request.parameters.entries.joinToString(", ") { (key, value) ->
        val keyEscaped = key.escaped
        when (value) {
            is String -> {
                // Properly escape string values to prevent injection
                val valueEscaped = value.escaped
                "\"$keyEscaped\" to \"$valueEscaped\""
            }
            is Number -> "\"$keyEscaped\" to $value"
            is Boolean -> "\"$keyEscaped\" to $value"
            else -> {
                // For other types, escape the string representation
                val valueEscaped = value.escaped
                "\"$keyEscaped\" to \"$valueEscaped\""
            }
        }
    }

    return """
        |// Classpath is supplied via kotlinc -classpath; no @file:DependsOn
        |import com.niloda.cot.domain.dsl.cot
        |import com.niloda.cot.domain.generate.generate
        |import com.niloda.cot.domain.generate.RenderParams
        |import com.niloda.cot.domain.generate.renderConfigurable
        |
        |// User's COT DSL code (must define 'cot' as Either<DomainError, Cot>)
        |${request.cotDslCode}
        |
        |// Unwrap Either and generate output
        |val cotInstance = cot.fold(ifLeft = { e -> System.err.println("Cot build error: ${'$'}e"); kotlin.system.exitProcess(1); throw RuntimeException("") }, ifRight = { it })
        |val params = RenderParams.of(mapOf($parametersMap))
        |val result = generate(cotInstance, params, ::renderConfigurable)
        |
        |result.fold(
        |    ifLeft = { error -> 
        |        System.err.println("Generation error: ${'$'}error")
        |        kotlin.system.exitProcess(1)
        |    },
        |    ifRight = { output -> println(output) }
        |)
    """.trimMargin()
}