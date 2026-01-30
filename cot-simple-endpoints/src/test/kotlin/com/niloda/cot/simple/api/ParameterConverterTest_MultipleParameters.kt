package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest_MultipleParameters {

    @Test
    fun `convertToRenderParams handles multiple parameters`() {
        val params = mapOf(
            "name" to JsonPrimitive("Alice"),
            "enabled" to JsonPrimitive(false),
            "count" to JsonPrimitive(3)
        )
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals("Alice", renderParams.get("name"))
        assertEquals(false, renderParams.get("enabled"))
        assertEquals(3, renderParams.get("count"))
    }
}
