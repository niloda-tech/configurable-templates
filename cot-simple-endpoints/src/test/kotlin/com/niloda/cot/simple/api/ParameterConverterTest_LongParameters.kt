package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest_LongParameters {

    @Test
    fun `convertToRenderParams handles long parameters within int range`() {
        val params = mapOf("value" to JsonPrimitive(100L))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(100, renderParams.get("value"))
    }
}
