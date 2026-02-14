package com.niloda.cot.sandbox.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SandboxResponseTest {

    @Test
    fun `can create response with output and timing`() {
        val response = SandboxResponse(
            output = "Hello, World!",
            executionTimeMs = 150,
            compilationTimeMs = 5000
        )
        
        assertEquals("Hello, World!", response.output)
        assertEquals(150, response.executionTimeMs)
        assertEquals(5000, response.compilationTimeMs)
    }

    @Test
    fun `can create response with empty output`() {
        val response = SandboxResponse(
            output = "",
            executionTimeMs = 10,
            compilationTimeMs = 100
        )
        
        assertEquals("", response.output)
        assertEquals(10, response.executionTimeMs)
        assertEquals(100, response.compilationTimeMs)
    }
}
