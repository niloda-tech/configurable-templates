package com.niloda.cot.sandbox.service.sandbox

/**
 * Result of executing a command.
 */
data class CommandResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)