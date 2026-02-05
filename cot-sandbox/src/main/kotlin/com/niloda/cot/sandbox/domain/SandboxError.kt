package com.niloda.cot.sandbox.domain

/**
 * Sealed interface representing all possible errors that can occur during sandbox execution.
 */
sealed interface SandboxError {
    /**
     * Error that occurs during Kotlin script compilation.
     * @param message The compilation error message
     * @param details Additional details about the compilation failure
     */
    data class CompilationError(
        val message: String,
        val details: String = ""
    ) : SandboxError

    /**
     * Error that occurs during script runtime execution.
     * @param message The runtime error message
     * @param stackTrace The stack trace if available
     */
    data class RuntimeError(
        val message: String,
        val stackTrace: String = ""
    ) : SandboxError

    /**
     * Error that occurs when execution exceeds the timeout limit.
     * @param timeoutSeconds The timeout limit that was exceeded
     */
    data class TimeoutError(
        val timeoutSeconds: Int
    ) : SandboxError

    /**
     * Error that occurs when Docker operations fail.
     * @param message The Docker error message
     * @param details Additional details about the Docker failure
     */
    data class DockerError(
        val message: String,
        val details: String = ""
    ) : SandboxError

    /**
     * Error that occurs during resource creation or cleanup.
     * @param message The resource error message
     */
    data class ResourceError(
        val message: String
    ) : SandboxError

    /**
     * Error that occurs when the COT template is invalid.
     * @param message The validation error message
     */
    data class InvalidTemplateError(
        val message: String
    ) : SandboxError
}
