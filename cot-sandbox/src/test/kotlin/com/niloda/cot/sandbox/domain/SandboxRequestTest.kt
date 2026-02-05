package com.niloda.cot.sandbox.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SandboxRequestTest {

    @Test
    fun `can create request with minimal parameters`() {
        val request = SandboxRequest(
            cotDslCode = "val cot = cot(\"Test\") { \"Hello\".text }"
        )
        
        assertEquals("val cot = cot(\"Test\") { \"Hello\".text }", request.cotDslCode)
        assertTrue(request.parameters.isEmpty())
        assertEquals(ExecutionConfig(), request.config)
    }

    @Test
    fun `can create request with parameters`() {
        val request = SandboxRequest(
            cotDslCode = "val cot = cot(\"Test\") { Params.name ifTrueThen \"text\" }",
            parameters = mapOf("name" to "value", "count" to 42)
        )
        
        assertEquals(2, request.parameters.size)
        assertEquals("value", request.parameters["name"])
        assertEquals(42, request.parameters["count"])
    }

    @Test
    fun `can create request with custom config`() {
        val config = ExecutionConfig(timeoutSeconds = 60)
        val request = SandboxRequest(
            cotDslCode = "val cot = cot(\"Test\") {}",
            config = config
        )
        
        assertEquals(60, request.config.timeoutSeconds)
    }
}
