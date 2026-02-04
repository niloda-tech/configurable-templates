package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest_StringParameters {

    @Test
    fun `convertToRenderParams handles string parameters`() {
        val params = mapOf("name" to JsonPrimitive("John"))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals("John", renderParams.get("name"))
    }
}
