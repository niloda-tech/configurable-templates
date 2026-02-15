package com.niloda.cot.sandbox.service

import com.niloda.cot.sandbox.domain.ExecutionConfig
import com.niloda.cot.sandbox.domain.SandboxError
import com.niloda.cot.sandbox.domain.SandboxRequest
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests that run template code in the real Docker sandbox image.
 *
 * Prerequisites:
 * - Docker installed and running
 * - cot-sandbox image built: `docker build -t cot-sandbox:latest .` (from cot-sandbox dir)
 * - cot-dsl fat JAR built: `./gradlew :cot-dsl:shadowJar`
 *
 * These tests are skipped if Docker is unavailable or the image/JAR are missing.
 * Run with: `./gradlew :cot-sandbox:test --tests "*DockerSandboxIntegration*"`
 */
@Tag("integration")
class DockerSandboxIntegrationTest {

    companion object {
        private const val DOCKER_IMAGE = "cot-sandbox:latest"
        private const val COT_DSL_JAR_NAME = "cot-dsl-1.0-SNAPSHOT-all.jar"

        lateinit var cotDslJarPath: Path
        lateinit var sandboxService: DockerSandboxService
        var dockerAvailable: Boolean = false

        @JvmStatic
        @BeforeAll
        fun assumeDockerAndImageAndJar() {
            cotDslJarPath = resolveCotDslJar()
            assumeTrue(Files.exists(cotDslJarPath)) {
                "cot-dsl JAR not found at $cotDslJarPath. Run: ./gradlew :cot-dsl:shadowJar"
            }
            dockerAvailable = isDockerAvailable()
            assumeTrue(dockerAvailable) {
                "Docker not available or image $DOCKER_IMAGE not found. Build with: docker build -t $DOCKER_IMAGE ."
            }
            assumeTrue(isDockerImagePresent()) {
                "Docker image $DOCKER_IMAGE not found. Build with: docker build -t $DOCKER_IMAGE . (from cot-sandbox dir)"
            }
            // Use project build dir so Docker bind-mount can see scripts (avoids /private/var and sync issues)
            val workDir = Paths.get("build/cot-sandbox-it").toAbsolutePath()
            Files.createDirectories(workDir)
            sandboxService = DockerSandboxService(
                dockerImage = DOCKER_IMAGE,
                cotDslJarPath = cotDslJarPath,
                workDir = workDir
            )
        }

        private fun resolveCotDslJar(): Path {
            val fromRoot = Paths.get("cot-dsl/build/libs/$COT_DSL_JAR_NAME")
            val fromSandbox = Paths.get("../cot-dsl/build/libs/$COT_DSL_JAR_NAME")
            return when {
                Files.exists(fromRoot) -> fromRoot.toAbsolutePath()
                Files.exists(fromSandbox) -> fromSandbox.toAbsolutePath()
                else -> fromRoot.toAbsolutePath()
            }
        }

        private fun isDockerAvailable(): Boolean {
            return try {
                val process = ProcessBuilder("docker", "version").start()
                process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) && process.exitValue() == 0
            } catch (_: Exception) {
                false
            }
        }

        private fun isDockerImagePresent(): Boolean {
            if (!dockerAvailable) return false
            return try {
                val process = ProcessBuilder(
                    "docker", "image", "inspect", DOCKER_IMAGE
                ).redirectErrorStream(true).start()
                process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) && process.exitValue() == 0
            } catch (_: Exception) {
                false
            }
        }
    }

    @Test
    fun `execute simple static template in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Hello") {
                    "Hello, World!".text
                }
            """.trimIndent(),
            parameters = emptyMap(),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        val response = result.getOrNull()!!
        assertEquals("Hello, World!", response.output.trim())
        assertTrue(response.executionTimeMs >= 0)
    }

    @Test
    fun `hello alice`() {
        val request = SandboxRequest(
            cotDslCode = """
               val cot = cot("Python Greeting Script") {
                    "#!/usr/bin/env python3".text
                    "\n".text
                    "name = \"".text
                    dynamic("name")
                    "\"\n".text
                    "print(f\"Hello {name}\")".text
                }
            """.trimIndent(),
            parameters = mapOf("name" to "Alice"),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        val response = result.getOrNull()!!
        assertEquals("""#!/usr/bin/env python3
name = "Alice"
print(f"Hello {name}")""", response.output.trim())
        assertTrue(response.executionTimeMs >= 0)
        result.fold({ println("Error: $it") }, { println("Success: $it") })

    }

    @Test
    fun `execute template with dynamic parameter in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Greeting") {
                    "Hello, ".text
                    dynamic("name")
                    "!".text
                }
            """.trimIndent(),
            parameters = mapOf("name" to "Alice"),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        assertEquals("Hello, Alice!", result.getOrNull()!!.output.trim())
        result.fold({ println("Error: $it") }, { println("Success: $it") })

    }

    @Test
    fun `execute conditional template in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Cond") {
                    conditional("enabled") {
                        static("Enabled!")
                    }
                }
            """.trimIndent(),
            parameters = mapOf("enabled" to true),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        assertEquals("Enabled!", result.getOrNull()!!.output.trim())
    }

    @Test
    fun `conditional false omits section in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Cond") {
                    conditional("enabled") {
                        static("Should not appear")
                    }
                }
            """.trimIndent(),
            parameters = mapOf("enabled" to false),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        assertEquals("", result.getOrNull()!!.output.trim())
    }

    @Test
    fun `repetition renders n times in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Repeat") {
                    repetition("n") {
                        static("X")
                    }
                }
            """.trimIndent(),
            parameters = mapOf("n" to 4),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        assertEquals("XXXX", result.getOrNull()!!.output.trim())
        result.fold({ println("Error: $it") }, { println("Success: $it") })
    }

    @Test
    fun `oneOf renders selected choice in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Pick") {
                    oneOf("choice") {
                        choice("a") { static("A") }
                        choice("b") { static("B") }
                        choice("c") { static("C") }
                    }
                }
            """.trimIndent(),
            parameters = mapOf("choice" to "b"),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        assertEquals("B", result.getOrNull()!!.output.trim())
    }

    @Test
    fun `blank template returns InvalidTemplateError`() {
        val request = SandboxRequest(
            cotDslCode = "   \n  ",
            parameters = emptyMap(),
            config = ExecutionConfig(timeoutSeconds = 30)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isLeft(), "Expected validation error, got: $result")
        val error = result.swap().getOrNull()!!
        assertTrue(error is SandboxError.InvalidTemplateError)
        assertNotNull((error as SandboxError.InvalidTemplateError).message)
    }

    @Test
    fun `invalid Kotlin syntax returns RuntimeError from container`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Broken") {
                    this is not valid kotlin
                }
            """.trimIndent(),
            parameters = emptyMap(),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isLeft(), "Expected failure for invalid syntax, got: $result")
        val error = result.swap().getOrNull()!!
        assertTrue(
            error is SandboxError.RuntimeError,
            "Expected RuntimeError (script failed in container), got: $error"
        )
    }

    @Test
    fun `execution respects timeout`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Slow") {
                    "done".text
                }
            """.trimIndent(),
            parameters = emptyMap(),
            config = ExecutionConfig(timeoutSeconds = 120)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success within timeout, got: $result")
        assertEquals("done", result.getOrNull()!!.output.trim())
    }

    @Test
    fun `numeric and boolean parameters work in Docker`() {
        val request = SandboxRequest(
            cotDslCode = """
                val cot = cot("Mixed") {
                    conditional("flag") {
                        static("yes")
                    }
                    repetition("count") {
                        static(".")
                    }
                }
            """.trimIndent(),
            parameters = mapOf(
                "flag" to true,
                "count" to 3
            ),
            config = ExecutionConfig(timeoutSeconds = 60)
        )
        val result = sandboxService.execute(request)
        assertTrue(result.isRight(), "Expected success, got: $result")
        assertEquals("yes...", result.getOrNull()!!.output.trim())
    }



}
