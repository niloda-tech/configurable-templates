package com.niloda.cot.sandbox.service.sandbox

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

