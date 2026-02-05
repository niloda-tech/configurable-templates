# COT Sandbox Module

A secure Docker-based sandbox environment for safely compiling and executing Configurable Templates (COT) using Kotlin scripts.

## Overview

The `cot-sandbox` module provides a sandboxed execution environment for COT templates. It isolates template execution in Docker containers with configurable resource limits (CPU, memory, timeout), ensuring safe execution of user-provided code.

## Architecture

```
┌─────────────────┐
│  SandboxService │  (Interface)
└────────┬────────┘
         │
         │ implements
         ▼
┌────────────────────────┐
│ DockerSandboxService   │
│                        │
│ 1. Create temp script  │
│ 2. Compile in Docker   │
│ 3. Execute in Docker   │
│ 4. Return output       │
│ 5. Cleanup resources   │
└────────────────────────┘
```

## Features

- **Isolated Execution**: Each template runs in a separate Docker container
- **Resource Limits**: Configurable CPU, memory, and timeout limits
- **Type-Safe Error Handling**: Uses Arrow-kt's `Either` for error management
- **Compilation Validation**: Validates Kotlin syntax before execution
- **Automatic Cleanup**: Temporary resources are cleaned up after execution
- **Network Isolation**: Containers run without network access (`--network none`)

## Domain Models

### SandboxRequest
```kotlin
data class SandboxRequest(
    val cotDslCode: String,              // COT DSL template code
    val parameters: Map<String, Any>,    // Template parameters
    val config: ExecutionConfig          // Execution configuration
)
```

### SandboxResponse
```kotlin
data class SandboxResponse(
    val output: String,                  // Generated output
    val executionTimeMs: Long,           // Execution time
    val compilationTimeMs: Long          // Compilation time
)
```

### ExecutionConfig
```kotlin
data class ExecutionConfig(
    val timeoutSeconds: Int = 30,        // Max execution time
    val memoryLimitMb: Int = 512,        // Max memory
    val cpuLimit: Double = 1.0           // CPU cores fraction
)
```

### SandboxError
```kotlin
sealed interface SandboxError {
    data class CompilationError(message: String, details: String)
    data class RuntimeError(message: String, stackTrace: String)
    data class TimeoutError(timeoutSeconds: Int)
    data class DockerError(message: String, details: String)
    data class ResourceError(message: String)
    data class InvalidTemplateError(message: String)
}
```

## Usage

### Prerequisites

1. **Docker** must be installed and running
2. Build the cot-dsl JAR:
   ```bash
   ./gradlew :cot-dsl:build
   ```
3. Build the Docker image:
   ```bash
   cd cot-sandbox
   docker build -t cot-sandbox:latest .
   ```

### Basic Usage

```kotlin
import com.niloda.cot.sandbox.service.DockerSandboxService
import com.niloda.cot.sandbox.domain.*
import java.nio.file.Paths

// Initialize the service
val cotDslJar = Paths.get("cot-dsl/build/libs/cot-dsl-1.0-SNAPSHOT.jar")
val sandboxService = DockerSandboxService(cotDslJarPath = cotDslJar)

// Create a request
val request = SandboxRequest(
    cotDslCode = """
        val cot = cot("Greeting") {
            "Hello, ".text
            Params.name ifTrueThen "World"
            "!".text
        }
    """.trimIndent(),
    parameters = mapOf("name" to "Alice"),
    config = ExecutionConfig(timeoutSeconds = 10)
)

// Execute the template
val result = sandboxService.execute(request)

result.fold(
    ifLeft = { error -> println("Error: $error") },
    ifRight = { response -> println("Output: ${response.output}") }
)
```

### Advanced Configuration

```kotlin
val config = ExecutionConfig(
    timeoutSeconds = 60,      // 1 minute timeout
    memoryLimitMb = 1024,     // 1GB memory limit
    cpuLimit = 2.0            // Use up to 2 CPU cores
)

val request = SandboxRequest(
    cotDslCode = complexTemplate,
    parameters = complexParams,
    config = config
)
```

## Docker Image

The Docker image (`cot-sandbox:latest`) provides:

- OpenJDK 17 (Eclipse Temurin)
- Kotlin Compiler 2.2.21
- Isolated filesystem with no network access
- Mounted volumes for:
  - Kotlin script file (read-only)
  - cot-dsl JAR dependency (read-only)

### Building the Image

```bash
cd cot-sandbox
docker build -t cot-sandbox:latest .
```

### Manual Docker Execution

```bash
# Compile and run a script manually
docker run --rm \
  -v /path/to/script.kts:/sandbox/scripts/script.kts:ro \
  -v /path/to/cot-dsl.jar:/sandbox/libs/cot-dsl.jar:ro \
  --network none \
  --memory 512m \
  --cpus 1.0 \
  cot-sandbox:latest \
  sh -c "cd /sandbox/scripts && kotlinc -script script.kts -classpath /sandbox/libs/cot-dsl.jar"
```

## Security Considerations

1. **Network Isolation**: Containers run with `--network none`
2. **Resource Limits**: CPU, memory, and timeout constraints prevent resource exhaustion
3. **Read-Only Mounts**: Script and JAR files are mounted as read-only
4. **Temporary Files**: Each execution uses isolated temporary files
5. **Process Isolation**: Docker provides process-level isolation

## Error Handling

The service follows functional programming principles using Arrow-kt:

```kotlin
sandboxService.execute(request).fold(
    ifLeft = { error ->
        when (error) {
            is SandboxError.CompilationError -> 
                handleCompilationError(error.message, error.details)
            is SandboxError.RuntimeError -> 
                handleRuntimeError(error.message, error.stackTrace)
            is SandboxError.TimeoutError -> 
                handleTimeout(error.timeoutSeconds)
            is SandboxError.DockerError -> 
                handleDockerError(error.message)
            is SandboxError.ResourceError -> 
                handleResourceError(error.message)
            is SandboxError.InvalidTemplateError -> 
                handleInvalidTemplate(error.message)
        }
    },
    ifRight = { response ->
        println("Success!")
        println("Output: ${response.output}")
        println("Execution: ${response.executionTimeMs}ms")
        println("Compilation: ${response.compilationTimeMs}ms")
    }
)
```

## Testing

```bash
# Run all tests
./gradlew :cot-sandbox:test

# Run with coverage report
./gradlew :cot-sandbox:test :cot-sandbox:jacocoTestReport

# View coverage report
open cot-sandbox/build/reports/jacoco/test/html/index.html
```

## Integration with Backend

To use the sandbox in the backend API:

1. Add dependency in `cot-simple-endpoints/build.gradle.kts`:
   ```kotlin
   implementation(project(":cot-sandbox"))
   ```

2. Create an API endpoint:
   ```kotlin
   post("/api/sandbox/execute") {
       val request = call.receive<SandboxRequest>()
       sandboxService.execute(request).fold(
           ifLeft = { error -> 
               call.respond(HttpStatusCode.BadRequest, error)
           },
           ifRight = { response -> 
               call.respond(HttpStatusCode.OK, response)
           }
       )
   }
   ```

## Limitations

- Requires Docker to be installed and running
- Network access is disabled for security
- File system access is limited to mounted volumes
- Kotlin script compilation can be slow (5-10 seconds)

## Future Enhancements

- [ ] Pre-compiled script caching to improve performance
- [ ] Support for multiple Kotlin versions
- [ ] Custom dependency injection
- [ ] Persistent compilation cache
- [ ] Metrics and monitoring integration
- [ ] Rate limiting and quotas

## Dependencies

- **Arrow-kt 2.2.0**: Functional error handling
- **Kotlin 2.2.21**: Language and compiler
- **SLF4J**: Logging
- **Docker**: Container runtime

## License

This module is part of the Configurable Templates project and follows the same MIT License.
