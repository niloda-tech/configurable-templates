package com.niloda.cot.domain.dsl

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.dsl.builders.ConcreteCotBuilder
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

fun cot(name: String): Either<DomainError, Cot> =
    either {
        // Basic name validation using Raise DSL helpers
        ensure(name.isNotBlank()) { DomainError.InvalidName("Cot.name", reason = "blank") }
        val builder = ConcreteCotBuilder()
            .apply {
                optional("x", Section("nice"))
            }
        Cot(name = name, schema = builder.build())
    }