package com.niloda.cot.simple.api

import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParameterConverterTest {

    @Test
    fun `convertToRenderParams handles string parameters`() {
        val params = mapOf("name" to JsonPrimitive("John"))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals("John", renderParams.get("name"))
    }

    @Test
    fun `convertToRenderParams handles boolean parameters`() {
        val params = mapOf("enabled" to JsonPrimitive(true))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(true, renderParams.get("enabled"))
    }

    @Test
    fun `convertToRenderParams handles integer parameters`() {
        val params = mapOf("count" to JsonPrimitive(5))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(5, renderParams.get("count"))
    }

    @Test
    fun `convertToRenderParams handles double parameters`() {
        val params = mapOf("ratio" to JsonPrimitive(3.14))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(3.14, renderParams.get("ratio"))
    }

    @Test
    fun `convertToRenderParams handles long parameters within int range`() {
        val params = mapOf("value" to JsonPrimitive(100L))
        val result = convertToRenderParams(params)
        
        assertTrue(result.isRight())
        val renderParams = result.getOrNull()!!
        assertEquals(100, renderParams.get("value"))
    }

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
