package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest_DoubleParameters {

    @Test
    fun `convertToRenderParams handles double parameters`() {
        val params = mapOf("ratio" to JsonPrimitive(3.14))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(3.14, renderParams.get("ratio"))
    }
}
