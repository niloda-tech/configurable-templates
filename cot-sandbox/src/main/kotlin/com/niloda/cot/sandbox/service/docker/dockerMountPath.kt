package com.niloda.cot.sandbox.service.docker

import java.nio.file.Path

/**
 * Path for Docker bind mount. On macOS, Path.toAbsolutePath() may return /private/var/...
 * but Docker expects /var/... so we strip the /private prefix.
 */
fun Path.dockerMountPath(): String {
    val s = toAbsolutePath().toString()
    return if (s.startsWith("/private") && s.length > 8) s.removePrefix("/private") else s
}