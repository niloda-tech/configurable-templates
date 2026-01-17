package com.niloda.cot.domain.dsl.actions

import arrow.core.raise.Raise
import com.niloda.cot.Param
import com.niloda.cot.domain.dsl.CotDsl
import com.niloda.cot.domain.dsl.builders.HasCotBuilder
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section


@CotDsl
interface LogicalDsl : HasCotBuilder {

    context(_: Raise<DomainError>)
    infix fun <T> Enum<T>.ifTrue(staticValue: String) where T : Enum<T>, T : Param<T> =
        builder.conditional(name, Section(staticValue))

    context(_: Raise<DomainError>)
    infix fun <T> Enum<T>.ifPresent(staticValue: String) where T : Enum<T>, T : Param<T> =
        builder.optional(name, Section(staticValue))

    context(_: Raise<DomainError>)
    infix fun Section.Part.whenParam(param: String) {
        builder.conditional(param, Section(this))
    }

    context(_: Raise<DomainError>)
    infix fun Section.Part.whenParam(param: Param<*>) {
        builder.conditional(param.toString(), Section(this))
    }

}