package com.niloda.cot.domain.dsl.actions

import com.niloda.cot.Param
import com.niloda.cot.domain.dsl.CotDsl
import com.niloda.cot.domain.dsl.builders.HasCotBuilder
import com.niloda.cot.domain.template.Section

@CotDsl
interface ParamsDsl: HasCotBuilder {

    operator fun <T> Enum<T>.unaryPlus() where T : Enum<T>, T : Param<T> {
        builder.dynamic(name)
    }

    operator fun <T> Enum<T>.unaryMinus(): Section.Part.Dynamic where T : Enum<T>, T : Param<T> =
        Section.Part.Dynamic(name)


}