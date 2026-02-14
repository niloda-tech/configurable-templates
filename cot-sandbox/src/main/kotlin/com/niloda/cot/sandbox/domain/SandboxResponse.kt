package com.niloda.cot.sandbox.domain

/**
 * Response from sandbox execution.
 * 
 * Note: For Kotlin scripts, compilation happens during execution rather than as a separate phase.
 * Therefore, `compilationTimeMs` is always 0, and `executionTimeMs` includes both compilation
 * and execution time.
 * 
 * @param output The generated output from the COT template
 * @param executionTimeMs Total execution time in milliseconds (includes compilation for Kotlin scripts)
 * @param compilationTimeMs Separate compilation time in milliseconds (always 0 for Kotlin scripts)
 */
data class SandboxResponse(
    val output: String,
    val executionTimeMs: Long,
    val compilationTimeMs: Long
)
