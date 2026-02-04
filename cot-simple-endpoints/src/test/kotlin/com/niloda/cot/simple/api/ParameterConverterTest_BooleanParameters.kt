package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest_BooleanParameters {

    @Test
    fun `convertToRenderParams handles boolean parameters`() {
        val params = mapOf("enabled" to JsonPrimitive(true))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(true, renderParams.get("enabled"))
    }
}
