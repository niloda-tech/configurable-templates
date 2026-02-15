package com.niloda.cot.sandbox.service.docker

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.sandbox.domain.SandboxError
import com.niloda.cot.sandbox.service.sandbox.CommandResult
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

/**
 * Executes a Docker command and returns the result.
 */
context(logger: Logger)
fun executeDockerCommand(
    command: List<String>,
    timeoutSeconds: Int
): Either<SandboxError, CommandResult> = either {
    try {
        val process = ProcessBuilder(command)
            .redirectErrorStream(false)
            .start()

        val completed = process.waitFor(timeoutSeconds.toLong(), TimeUnit.SECONDS)

        ensure(completed) {
            process.destroyForcibly()
            logger.error("Process timed out after {} seconds", timeoutSeconds)
            SandboxError.TimeoutError(timeoutSeconds)
        }

        val stdout = process.inputStream.bufferedReader().readText()
        val stderr = process.errorStream.bufferedReader().readText()
        val exitCode = process.exitValue()

        CommandResult(stdout, stderr, exitCode)
    } catch (e: Exception) {
        logger.error("Docker command execution failed", e)
        raise(
            SandboxError.DockerError(
                message = "Failed to execute Docker command: ${e.message}",
                details = e.stackTraceToString()
            )
        )
    }
}