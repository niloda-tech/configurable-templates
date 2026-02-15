package com.niloda.cot.sandbox.service

import com.niloda.cot.sandbox.domain.ExecutionConfig
import com.niloda.cot.sandbox.service.DockerSandboxService.dockerMountPath
import java.nio.file.Path

/**
 * Builds a Docker command with resource limits.
 * Mounts parent directories (not single files) so that on Docker for Mac bind mounts
 * show real files; single-file mounts can appear as directories.
 */
fun buildDockerCommand(
    scriptPath: Path,
    dockerImage: String,
    command: String,
    config: ExecutionConfig,
    cotDslJarPath: Path
): List<String> {
    val scriptsDir = scriptPath.parent.dockerMountPath()
    val libsDir = cotDslJarPath.parent.dockerMountPath()
    return listOf(
        "docker", "run",
        "--rm",
        "--network", "none",
        "--memory", "${config.memoryLimitMb}m",
        "--cpus", config.cpuLimit.toString(),
        "-v", "$scriptsDir:/sandbox/scripts:ro",
        "-v", "$libsDir:/sandbox/libs:ro",
        "-w", "/sandbox/scripts",
        dockerImage,
        "sh", "-c", command
    )
}