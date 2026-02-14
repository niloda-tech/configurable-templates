package com.niloda.cot.sandbox.domain

/**
 * Configuration for sandbox execution.
 * @param timeoutSeconds Maximum execution time in seconds (default: 30)
 * @param memoryLimitMb Maximum memory limit in MB (default: 512)
 * @param cpuLimit CPU limit as fraction of a single core (default: 1.0)
 */
data class ExecutionConfig(
    val timeoutSeconds: Int = 30,
    val memoryLimitMb: Int = 512,
    val cpuLimit: Double = 1.0
) {
    init {
        require(timeoutSeconds > 0) { "Timeout must be positive" }
        require(memoryLimitMb > 0) { "Memory limit must be positive" }
        require(cpuLimit > 0) { "CPU limit must be positive" }
    }
}
