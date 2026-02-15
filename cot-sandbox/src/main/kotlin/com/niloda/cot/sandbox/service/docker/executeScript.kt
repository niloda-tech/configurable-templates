package com.niloda.cot.sandbox.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.sandbox.domain.ExecutionConfig
import com.niloda.cot.sandbox.domain.SandboxError
import java.nio.file.Path

/**
 * Executes the Kotlin script using Docker.
 * Note: Kotlin scripts are compiled during execution, so compilation errors
 * will appear as runtime errors in the stderr output.
 */
context(logger: org.slf4j.Logger)
fun executeScript(
    scriptPath: String,
    command : List<String>,
    timeoutSeconds: Int

): Either<SandboxError, String> = either {

    logger.debug("Executing script: {}", scriptPath)
    val result = executeDockerCommand(command, timeoutSeconds).bind()
    ensure(result.exitCode == 0) {
        logger.error("Execution failed: {}", result.stderr)
        SandboxError.RuntimeError(
            message = "Script execution failed",
            stackTrace = result.stderr
        )
    }

    result.stdout
}