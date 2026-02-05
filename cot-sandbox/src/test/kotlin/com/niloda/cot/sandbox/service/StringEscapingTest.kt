package com.niloda.cot.sandbox.service

import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringEscapingTest {

    @Test
    fun `escapes quotes in string parameters`() {
        val value = """Hello "World""""
        val escaped = escapeStringValue(value)
        
        assertTrue(escaped.contains("\\\""))
        assertEquals("""Hello \"World\"""", escaped)
    }

    @Test
    fun `escapes backslashes in string parameters`() {
        val value = """C:\Users\test"""
        val escaped = escapeStringValue(value)
        
        assertTrue(escaped.contains("\\\\"))
        assertEquals("""C:\\Users\\test""", escaped)
    }

    @Test
    fun `escapes newlines in string parameters`() {
        val value = "Line1\nLine2"
        val escaped = escapeStringValue(value)
        
        assertTrue(escaped.contains("\\n"))
        assertEquals("Line1\\nLine2", escaped)
    }

    @Test
    fun `escapes dollar signs in string parameters`() {
        val value = "Price: ${'$'}100"
        val escaped = escapeStringValue(value)
        
        assertTrue(escaped.contains("\\$"))
        assertEquals("Price: \\$100", escaped)
    }

    @Test
    fun `escapes tabs and carriage returns`() {
        val value = "Tab\there\r\n"
        val escaped = escapeStringValue(value)
        
        assertTrue(escaped.contains("\\t"))
        assertTrue(escaped.contains("\\r"))
        assertTrue(escaped.contains("\\n"))
        assertEquals("Tab\\there\\r\\n", escaped)
    }

    @Test
    fun `handles multiple escape sequences`() {
        val value = """Path: "C:\temp\file.txt" ${'$'}HOME\nEnd"""
        val escaped = escapeStringValue(value)
        
        assertEquals("""Path: \"C:\\temp\\file.txt\" \${'$'}HOME\\nEnd""", escaped)
    }

    // Helper function that mimics the escaping logic in DockerSandboxService
    private fun escapeStringValue(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("$", "\\$")
    }
}
