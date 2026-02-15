package com.niloda.cot.sandbox.service.docker

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.sandbox.domain.SandboxError
import com.niloda.cot.sandbox.service.sandbox.CommandResult
import org.slf4j.Logger
import java.nio.file.Path

/**
 * Executes the Kotlin script using Docker.
 * Note: Kotlin scripts are compiled during execution, so compilation errors
 * will appear as runtime errors in the stderr output.
 */
context(logger: Logger)
fun executeScript(
    scriptPath: Path,
    command: List<String>,
    timeoutSeconds: Int,
    executeCommand: context(Logger) (List<String>, Int) -> Either<SandboxError, CommandResult>
): Either<SandboxError, String> = either {

    logger.debug("Executing script: {}", scriptPath)
    val result = executeCommand(command, timeoutSeconds).bind()
    ensure(result.exitCode == 0) {
        logger.error("Execution failed: {}", result.stderr)
        SandboxError.RuntimeError(
            message = "Script execution failed",
            stackTrace = result.stderr
        )
    }

    result.stdout
}