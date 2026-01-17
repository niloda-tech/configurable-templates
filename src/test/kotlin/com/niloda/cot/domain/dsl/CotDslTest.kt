package com.niloda.cot.domain.dsl

import arrow.core.Either
import com.niloda.cot.Params
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.dsl.actions.decoratedCot
import com.niloda.cot.domain.dsl.builders.ConcreteCotBuilder
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.template.Configurable
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section.Part.Dynamic
import com.niloda.cot.domain.template.Section.Part.Static
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
        assertEquals(
            1,
            cot.schema.size,
            "Expected one Unconditional section"

        )
    }

    @Test
    fun `top-level unary plus collects unconditional content`() {
        val result = ConcreteCotBuilder("T").withExt() {
            +"Template"
            +"Line 2"
            Static("X") whenParam "enabled"
        }

        assertTrue(result.isRight())
        val cot = result.getOrNull()!!
        assertEquals(
            2,
            cot.schema.size,
            "Expected two Configurable sections, static and dynamic"
        )
        assertEquals(
            2,
            cot.schema
                .filterIsInstance<Configurable.Unconditional>()
                .flatMap { it.section.parts }
                .size,
            "Expected two static parts in Unconditional section"
        )
    }

    @Test
    fun `rem operator creates Dynamic part from Param enum`() {
        val result = decoratedCot("T") {
            // `%` should convert the enum Param to a Dynamic part using its name
            Dynamic("static text").rem(Params.NAME) whenParam Params.x
        }

        listOf(
            true to "name",
            false to ""
        ).forEach { (x, expected) ->
            val gen = generate(
                result.getOrNull()!!, RenderParams.of(
                    "x" to x,
                    "NAME" to "name"
                )
            )
            assertEquals(expected, gen.getOrNull())
        }

        assertTrue(result.isRight())
        val cot = result.getOrNull()!!

        val conditional = cot.schema.filterIsInstance<Configurable.Conditional>().single()
        assertEquals(
            "x",
            conditional.parameterName,
            "Expected a single Conditional configurable for parameter x"
            )

        val parts = conditional.section.parts
        assertEquals(1, parts.size)
        val dynamic = parts.single() as Dynamic
        assertEquals(
            "NAME",
            dynamic.parameterName,
            "Expected Dynamic part with parameter name NAME"
        )

    }
}
