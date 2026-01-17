package com.niloda.cot.domain.dsl.actions

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.dsl.builders.ConcreteCotBuilder
import com.niloda.cot.domain.dsl.builders.DecoratedBuilder
import com.niloda.cot.domain.template.DomainError

fun decoratedCot(
    name: String,
    configure: context(Raise<DomainError>) DecoratedBuilder.() -> Unit
): Either<DomainError, Cot> =
    either {
        Cot(
            name = name,
            schema = DecoratedBuilder(builder = ConcreteCotBuilder())
                .apply{ configure() }
                .build()
        )
    }