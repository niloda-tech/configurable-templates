package com.niloda.cot.sandbox.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.sandbox.domain.ExecutionConfig
import com.niloda.cot.sandbox.domain.SandboxError
import com.niloda.cot.sandbox.domain.SandboxRequest
import com.niloda.cot.sandbox.domain.SandboxResponse
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.deleteIfExists

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

    private val logger = LoggerFactory.getLogger(DockerSandboxService::class.java)

    override fun execute(request: SandboxRequest): Either<SandboxError, SandboxResponse> = either {
        logger.debug("Executing sandbox request with config: {}", request.config)
        
        val startTime = System.currentTimeMillis()
        
        // Validate the request
        ensure(request.cotDslCode.isNotBlank()) {
            SandboxError.InvalidTemplateError("COT DSL code cannot be blank")
        }
        
        // Create temporary script file
        val scriptPath = createScriptFile(request).bind()
        
        try {
            // Validate the script (lightweight check)
            compileScript(scriptPath, request.config).bind()
            
            // Execute the script (includes compilation)
            val executionStart = System.currentTimeMillis()
            val output = executeScript(scriptPath, request.config).bind()
            val executionTime = System.currentTimeMillis() - executionStart
            
            val totalTime = System.currentTimeMillis() - startTime
            logger.debug("Execution completed in {}ms (total execution time: {}ms)",
                totalTime, executionTime)
            
            SandboxResponse(
                output = output,
                executionTimeMs = executionTime,
                compilationTimeMs = 0  // Compilation happens during execution for Kotlin scripts
            )
        } finally {
            // Clean up temporary files
            cleanupScript(scriptPath)
        }
    }

    /**
     * Creates a temporary Kotlin script file with the COT DSL code.
     */
    private fun createScriptFile(request: SandboxRequest): Either<SandboxError, Path> = either {
        try {
            val scriptContent = buildScriptContent(request)
            val scriptFile = Files.createTempFile(workDir, "cot-script-", ".kts")
            Files.writeString(scriptFile, scriptContent)
            logger.debug("Created script file: {}", scriptFile)
            scriptFile
        } catch (e: Exception) {
            logger.error("Failed to create script file", e)
            raise(SandboxError.ResourceError("Failed to create script file: ${e.message}"))
        }
    }

    /**
     * Builds the Kotlin script content with imports and execution logic.
     */
    private fun buildScriptContent(request: SandboxRequest): String {
        val parametersMap = request.parameters.entries.joinToString(", ") { (key, value) ->
            val keyEscaped = key.replace("\\", "\\\\").replace("\"", "\\\"")
            when (value) {
                is String -> {
                    // Properly escape string values to prevent injection
                    val valueEscaped = value.toString()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                        .replace("$", "\\$")
                    "\"$keyEscaped\" to \"$valueEscaped\""
                }
                is Number -> "\"$keyEscaped\" to $value"
                is Boolean -> "\"$keyEscaped\" to $value"
                else -> {
                    // For other types, escape the string representation
                    val valueEscaped = value.toString()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                        .replace("$", "\\$")
                    "\"$keyEscaped\" to \"$valueEscaped\""
                }
            }
        }

        return """
            |@file:DependsOn("cot-dsl-1.0-SNAPSHOT.jar")
            |
            |import com.niloda.cot.domain.dsl.cot
            |import com.niloda.cot.domain.generate.generate
            |import com.niloda.cot.domain.generate.RenderParams
            |import com.niloda.cot.domain.generate.renderConfigurable
            |
            |// User's COT DSL code
            |${request.cotDslCode}
            |
            |// Generate output with provided parameters
            |val params = RenderParams.of(mapOf($parametersMap))
            |val result = cot.generate(params, ::renderConfigurable)
            |
            |result.fold(
            |    ifLeft = { error -> 
            |        System.err.println("Generation error: ${'$'}error")
            |        kotlin.system.exitProcess(1)
            |    },
            |    ifRight = { output -> println(output) }
            |)
        """.trimMargin()
    }

    /**
     * Compiles the Kotlin script using Docker.
     * Note: Kotlin scripts are compiled during execution, so we skip separate compilation.
     * This avoids issues with compilation flags and ensures consistency with execution.
     */
    private fun compileScript(scriptPath: Path, config: ExecutionConfig): Either<SandboxError, Unit> = either {
        logger.debug("Skipping separate compilation - Kotlin scripts compile during execution")
        // Kotlin scripts are compiled when executed, so we don't need a separate compilation step
        // Compilation errors will be caught during execution
    }

    /**
     * Executes the Kotlin script using Docker.
     */
    private fun executeScript(scriptPath: Path, config: ExecutionConfig): Either<SandboxError, String> = either {
        logger.debug("Executing script: {}", scriptPath)
        
        val executeCommand = buildDockerCommand(
            scriptPath = scriptPath,
            command = "kotlinc -script ${scriptPath.fileName} -classpath /sandbox/libs/cot-dsl.jar",
            config = config,
            checkOnly = false
        )

        val result = executeDockerCommand(executeCommand, config.timeoutSeconds).bind()
        
        ensure(result.exitCode == 0) {
            logger.error("Execution failed: {}", result.stderr)
            SandboxError.RuntimeError(
                message = "Script execution failed",
                stackTrace = result.stderr
            )
        }
        
        result.stdout
    }

    /**
     * Builds a Docker command with resource limits.
     */
    private fun buildDockerCommand(
        scriptPath: Path,
        command: String,
        config: ExecutionConfig,
        checkOnly: Boolean
    ): List<String> {
        return listOf(
            "docker", "run",
            "--rm",
            "--network", "none",
            "--memory", "${config.memoryLimitMb}m",
            "--cpus", config.cpuLimit.toString(),
            "-v", "${scriptPath.toAbsolutePath()}:/sandbox/scripts/${scriptPath.fileName}:ro",
            "-v", "${cotDslJarPath.toAbsolutePath()}:/sandbox/libs/cot-dsl.jar:ro",
            "-w", "/sandbox/scripts",
            dockerImage,
            "sh", "-c", command
        )
    }

    /**
     * Executes a Docker command and returns the result.
     */
    private fun executeDockerCommand(
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
            raise(SandboxError.DockerError(
                message = "Failed to execute Docker command: ${e.message}",
                details = e.stackTraceToString()
            ))
        }
    }

    /**
     * Cleans up temporary script files.
     */
    private fun cleanupScript(scriptPath: Path) {
        try {
            scriptPath.deleteIfExists()
            logger.debug("Cleaned up script file: {}", scriptPath)
        } catch (e: Exception) {
            logger.warn("Failed to clean up script file: {}", scriptPath, e)
        }
    }

    /**
     * Result of executing a command.
     */
    private data class CommandResult(
        val stdout: String,
        val stderr: String,
        val exitCode: Int
    )
}
