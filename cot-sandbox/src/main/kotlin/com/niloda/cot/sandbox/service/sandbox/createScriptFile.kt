package com.niloda.cot.sandbox.service.sandbox

import arrow.core.Either
import arrow.core.raise.either
import com.niloda.cot.sandbox.domain.SandboxError
import org.slf4j.Logger
import java.nio.file.Files
import java.nio.file.Path

/**
 * Creates a temporary Kotlin script file with the COT DSL code.
 */
context(logger: Logger)
fun createScriptFile(
    scriptContent: String,
    workDir: Path
): Either<SandboxError, Path> =
    either {
        try {
            val scriptFile = Files.createTempFile(workDir, "cot-script-", ".kts")
            Files.writeString(scriptFile, scriptContent)
            logger.debug("Created script file: {}", scriptFile)
            scriptFile
        } catch (e: Exception) {
            logger.error("Failed to create script file", e)
            raise(SandboxError.ResourceError("Failed to create script file: ${e.message}"))
        }
    }