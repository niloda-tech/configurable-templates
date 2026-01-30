package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest_IntegerParameters {

    @Test
    fun `convertToRenderParams handles integer parameters`() {
        val params = mapOf("count" to JsonPrimitive(5))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(5, renderParams.get("count"))
    }
}
