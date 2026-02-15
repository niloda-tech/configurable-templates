package com.niloda.cot.sandbox.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.sandbox.domain.SandboxError
import com.niloda.cot.sandbox.domain.SandboxRequest
import com.niloda.cot.sandbox.domain.SandboxResponse
import com.niloda.cot.sandbox.service.docker.buildDockerCommand
import com.niloda.cot.sandbox.service.docker.executeDockerCommand
import com.niloda.cot.sandbox.service.docker.executeScript
import com.niloda.cot.sandbox.service.sandbox.buildScriptContent
import com.niloda.cot.sandbox.service.sandbox.cleanupScript
import com.niloda.cot.sandbox.service.sandbox.createScriptFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

/**
 * Docker-based implementation of SandboxService that executes Kotlin scripts in isolated containers.
 * 
 * This service:
 * 1. Creates a temporary Kotlin script file with the COT DSL code
 * 2. Builds the cot-dsl JAR if needed
 * 3. Runs the script in a Docker container with resource limits
 * 4. Captures the output or errors
 * 5. Cleans up temporary resources
 */
class DockerSandboxService(
    private val dockerImage: String = "cot-sandbox:latest",
    private val cotDslJarPath: Path,
    private val workDir: Path = Files.createTempDirectory("cot-sandbox")

) : SandboxService {


    override fun execute(request: SandboxRequest): Either<SandboxError, SandboxResponse> =
        with(LoggerFactory.getLogger(DockerSandboxService::class.java)) {
            executeWith(request)
        }

    context(logger: Logger)
    fun executeWith(request: SandboxRequest): Either<SandboxError, SandboxResponse> = either {
        logger.debug("Executing sandbox request with config: {}", request.config)

        val startTime = System.currentTimeMillis()

        // Validate the request
        ensure(request.cotDslCode.isNotBlank()) {
            SandboxError.InvalidTemplateError("COT DSL code cannot be blank")
        }

        // Create temporary script file
        val scriptPath = createScriptFile(
            scriptContent = buildScriptContent(request),
            workDir = workDir
        ).bind()

        val command = buildDockerCommand(
            scriptPath = scriptPath,
            dockerImage = dockerImage,
            config = request.config,
            jarFile = cotDslJarPath
        )

        try {
            // Execute the script (includes compilation for Kotlin scripts)
            val executionStart = System.currentTimeMillis()
            val output =
                executeScript(
                    scriptPath = scriptPath,
                    command = command,
                    timeoutSeconds = request.config.timeoutSeconds
                ) { command, timeout ->
                    executeDockerCommand(command, timeout)
                }.bind()

            val executionTime = System.currentTimeMillis() - executionStart

            val totalTime = System.currentTimeMillis() - startTime
            logger.debug(
                "Execution completed in {}ms (total execution time: {}ms)",
                totalTime, executionTime
            )

            SandboxResponse(
                output = output,
                executionTimeMs = executionTime,  // Includes compilation time for Kotlin scripts
                compilationTimeMs = 0  // No separate compilation phase for Kotlin scripts
            )
        } finally {
            // Clean up temporary files
            cleanupScript(scriptPath)
        }
    }

}

