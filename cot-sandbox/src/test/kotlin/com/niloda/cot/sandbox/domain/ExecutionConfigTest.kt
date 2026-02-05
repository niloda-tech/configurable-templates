package com.niloda.cot.sandbox.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ExecutionConfigTest {

    @Test
    fun `default config has reasonable values`() {
        val config = ExecutionConfig()
        
        assertEquals(30, config.timeoutSeconds)
        assertEquals(512, config.memoryLimitMb)
        assertEquals(1.0, config.cpuLimit)
    }

    @Test
    fun `custom config can be created`() {
        val config = ExecutionConfig(
            timeoutSeconds = 60,
            memoryLimitMb = 1024,
            cpuLimit = 2.0
        )
        
        assertEquals(60, config.timeoutSeconds)
        assertEquals(1024, config.memoryLimitMb)
        assertEquals(2.0, config.cpuLimit)
    }

    @Test
    fun `timeout must be positive`() {
        assertThrows<IllegalArgumentException> {
            ExecutionConfig(timeoutSeconds = 0)
        }
        
        assertThrows<IllegalArgumentException> {
            ExecutionConfig(timeoutSeconds = -1)
        }
    }

    @Test
    fun `memory limit must be positive`() {
        assertThrows<IllegalArgumentException> {
            ExecutionConfig(memoryLimitMb = 0)
        }
        
        assertThrows<IllegalArgumentException> {
            ExecutionConfig(memoryLimitMb = -100)
        }
    }

    @Test
    fun `cpu limit must be positive`() {
        assertThrows<IllegalArgumentException> {
            ExecutionConfig(cpuLimit = 0.0)
        }
        
        assertThrows<IllegalArgumentException> {
            ExecutionConfig(cpuLimit = -1.0)
        }
    }
}
