package com.niloda.cot.sandbox.service.docker

import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

/**
 * Cleans up temporary script files.
 */
context(logger: Logger)
fun cleanupScript(scriptPath: Path) {
    try {
        scriptPath.deleteIfExists()
        logger.debug("Cleaned up script file: {}", scriptPath)
    } catch (e: Exception) {
        logger.warn("Failed to clean up script file: {}", scriptPath, e)
    }
}

/**
 * Path for Docker bind mount. On macOS, Path.toAbsolutePath() may return /private/var/...
 * but Docker expects /var/... so we strip the /private prefix.
 */
fun Path.dockerMountPath(): String {
    val s = toAbsolutePath().toString()
    return if (s.startsWith("/private") && s.length > 8) s.removePrefix("/private") else s
}