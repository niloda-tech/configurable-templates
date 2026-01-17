package com.niloda.cot.domain.dsl.actions

import arrow.core.raise.Raise
import com.niloda.cot.domain.dsl.CotDsl
import com.niloda.cot.domain.dsl.builders.HasCotBuilder
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

@CotDsl
interface OperatorDsl : HasCotBuilder {

    operator fun String.unaryPlus() {
        builder.text(this)
    }

    context(_: Raise<DomainError>)
    operator fun String.compareTo(output: Section): Int {
        builder.conditional(this, output)
        return 0
    }

    operator fun String.unaryMinus(): Section.Part.Static =
        Section.Part.Static(this)

}

