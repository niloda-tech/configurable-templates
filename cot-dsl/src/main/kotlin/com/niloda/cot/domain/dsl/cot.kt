package com.niloda.cot.domain.dsl

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.dsl.builders.ConcreteCotBuilder
import com.niloda.cot.domain.dsl.builders.DecoratedBuilder
import com.niloda.cot.domain.template.DomainError

fun cot(
    name: String,
    configure: context(Raise<DomainError>) DecoratedBuilder.() -> Unit
): Either<DomainError, Cot> =
    either {
        ensure(name.isNotEmpty()) {
            DomainError.InvalidName("cot constructor", "templater name cannot be empty")
        }
        Cot(
            name = name,
            schema = DecoratedBuilder(builder = ConcreteCotBuilder())
                .apply{ configure() }
                .build()
        )
    }