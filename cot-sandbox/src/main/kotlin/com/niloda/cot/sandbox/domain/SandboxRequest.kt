package com.niloda.cot.sandbox.domain

/**
 * Request to execute a COT template in the sandbox.
 * @param cotDslCode The Kotlin DSL code defining the COT template
 * @param parameters Map of parameter names to values for template rendering
 * @param config Execution configuration (timeout, resource limits)
 */
data class SandboxRequest(
    val cotDslCode: String,
    val parameters: Map<String, Any> = emptyMap(),
    val config: ExecutionConfig = ExecutionConfig()
)
