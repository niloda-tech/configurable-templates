package com.example.library

import arrow.core.left
import arrow.core.right
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SafeCalculatorTest {

    @Test
    fun `division by non-zero should be successful`() {
        val result = SafeCalculator.safeDivide(10, 2)
        assertEquals(5.right(), result)
    }

    @Test
    fun `division by zero should return error`() {
        val result = SafeCalculator.safeDivide(10, 0)
        assertEquals(CalculatorError.DivisionByZero.left(), result)
    }
}
