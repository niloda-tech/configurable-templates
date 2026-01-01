package com.niloda.cot.domain.generate

import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.template.DomainError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GenerateTest {

    @Test
    fun conditional_true_renders_section() {
        val cot = cot("T") {
            conditional("enabled") {
                static("Hello")
            }
        }.getOrElse { error("Cot build failed: $it") }

        val out = generate(cot, RenderParams.of(mapOf("enabled" to true)))
        val s = out.getOrElse { error("Generation failed: $it") }
        assertEquals("Hello", s)
    }

    @Test
    fun conditional_false_omits_section() {
        val cot = cot("T") {
            conditional("enabled") { static("X") }
        }.getOrElse { error("Cot build failed: $it") }

        val out = generate(cot, RenderParams.of(mapOf("enabled" to false)))
        val s = out.getOrElse { error("Generation failed: $it") }
        assertEquals("", s)
    }

    @Test
    fun repetition_count_renders_n_times() {
        val cot = cot("T") {
            repetition("n") { static("A") }
        }.getOrElse { error("Cot build failed: $it") }

        val out = generate(cot, RenderParams.of(mapOf("n" to 3)))
        val s = out.getOrElse { error("Generation failed: $it") }
        assertEquals("AAA", s)
    }

    @Test
    fun repetition_zero_renders_empty() {
        val cot = cot("T") {
            repetition("n") { static("A") }
        }.getOrElse { error("Cot build failed: $it") }

        val out = generate(cot, RenderParams.of(mapOf("n" to 0)))
        val s = out.getOrElse { error("Generation failed: $it") }
        assertEquals("", s)
    }

    @Test
    fun oneOf_renders_selected_choice() {
        val cot = cot("T") {
            oneOf("choice") {
                choice("a") { static("A") }
                choice("b") { static("B") }
            }
        }.getOrElse { error("Cot build failed: $it") }

        val out = generate(cot, RenderParams.of(mapOf("choice" to "b")))
        val s = out.getOrElse { error("Generation failed: $it") }
        assertEquals("B", s)
    }

    @Test
    fun missing_parameter_reports_error() {
        val cot = cot("T") {
            conditional("enabled") { static("X") }
        }.getOrElse { error("Cot build failed: $it") }

        val res = generate(cot, RenderParams.empty())
        res.fold(
            { e -> assertTrue(e is DomainError.MissingParameter) },
            { _ -> error("Expected failure for missing parameter") }
        )
    }

    @Test
    fun type_mismatch_reports_error() {
        val cot = cot("T") {
            conditional("enabled") { static("X") }
        }.getOrElse { error("Cot build failed: $it") }

        val res = generate(cot, RenderParams.of(mapOf("enabled" to "yes")))
        res.fold(
            { e -> assertTrue(e is DomainError.TypeMismatch) },
            { _ -> error("Expected failure for type mismatch") }
        )
    }

    @Test
    fun oneOf_unknown_key_reports_error() {
        val cot = cot("T") {
            oneOf("choice") {
                choice("a") { static("A") }
                choice("b") { static("B") }
            }
        }.getOrElse { error("Cot build failed: $it") }

        val res = generate(cot, RenderParams.of(mapOf("choice" to "c")))
        res.fold(
            { e -> assertTrue(e is DomainError.InvalidChoiceKey) },
            { _ -> error("Expected failure for invalid choice key") }
        )
    }
}
