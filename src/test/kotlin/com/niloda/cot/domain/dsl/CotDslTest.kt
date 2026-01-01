package com.niloda.cot.domain.dsl

import arrow.core.Either
import arrow.core.left
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.template.DomainError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class CotDslTest {

    @Test
    fun `happy path builds Cot with one conditional`() {
        val result: Either<DomainError, Cot> = cot("MyTemplate") {
            conditional("enabled") {
                section {
                    static("Hello, ")
                    dynamic("name")
                }
            }
        }

        assertTrue(result.isRight())
        val cot = result.getOrNull()!!
        assertEquals("MyTemplate", cot.name)
        assertEquals(1, cot.schema.size)
    }

    @Test
    fun `blank template name returns InvalidName`() {
        val result = cot("") { }

        assertTrue(result.isLeft())
        val err = result.swap().getOrNull()!!
        assertTrue(err is DomainError.InvalidName)
    }

    @Test
    fun `oneOf with duplicate choice keys returns DuplicateKey`() {
        val result = cot("T") {
            oneOf("pick") {
                choice("A") { static("a") }
                choice("A") { static("duplicate") }
            }
        }

        assertTrue(result.isLeft())
        val err = result.swap().getOrNull()!!
        assertTrue(err is DomainError.DuplicateKey)
    }

    @Test
    fun `section builder supports unary plus for static text`() {
        val result: Either<DomainError, Cot> = cot("T") {
            conditional("enabled") {
                section {
                    +"Hello, "
                    dynamic("name")
                }
            }
        }

        assertTrue(result.isRight())
        val cot = result.getOrNull()!!
        assertEquals(1, cot.schema.size)
    }

    @Test
    fun `top-level unary plus collects unconditional content`() {
        val result = cot("T") {
            +"Template\n"
            +"Line 2\n"
            conditional("enabled") { static("X") }
        }

        assertTrue(result.isRight())
        val cot = result.getOrNull()!!
        // first item should be Unconditional followed by Conditional
        assertEquals(2, cot.schema.size)
    }
}
