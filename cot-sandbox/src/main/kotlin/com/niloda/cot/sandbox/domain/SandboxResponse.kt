package com.niloda.cot.sandbox.domain

/**
 * Response from sandbox execution.
 * @param output The generated output from the COT template
 * @param executionTimeMs Execution time in milliseconds
 * @param compilationTimeMs Compilation time in milliseconds
 */
data class SandboxResponse(
    val output: String,
    val executionTimeMs: Long,
    val compilationTimeMs: Long
)
