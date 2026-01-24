package com.niloda.cot.domain.dsl.actions

import arrow.core.raise.Raise
import com.niloda.cot.Param
import com.niloda.cot.Params
import com.niloda.cot.domain.dsl.CotDsl
import com.niloda.cot.domain.dsl.builders.HasCotBuilder
import com.niloda.cot.domain.template.Configurable.IfPresent
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

fun <T:Param<T>> substituteToken(param: T): String = "{{${param.parameterName()}}}"

@CotDsl
interface LogicalDsl : HasCotBuilder {

    context(_: Raise<DomainError>)
    fun <T> T.ifPresentThen(
        paramMapping: (T) -> String
    ): IfPresent where T : Param<T> =
        builder.optional(
            parameterName = parameterName(),
            section = Section( paramMapping(this))
        )



    data class WrapParam<T>( val param: Param<T>) where T: Param<T>, T: Enum<T> {
        override fun toString(): String = "%$param%"
    }

    context(_: Raise<DomainError>)
    fun <T, T1> Param<T>.ifPresentThen1(
        param1: Param<T1>,
        thenParamValue: (WrapParam<T1>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>, T1 : Enum<T1>, T1 : Param<T1> {
        return builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(
                WrapParam(param1)
            ))
        )
    }

    context(_: Raise<DomainError>)
    fun <T, T1, T2> Param<T>.ifPresentThen2(
        param1: Param<T1>,
        param2: Param<T2>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(WrapParam(param1), WrapParam(param2)))
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3> Param<T>.ifPresentThen3(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(WrapParam(param1), WrapParam(param2), WrapParam(param3)))
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4> Param<T>.ifPresentThen4(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(WrapParam(param1), WrapParam(param2), WrapParam(param3), WrapParam(param4)))
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4, T5> Param<T>.ifPresentThen5(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        param5: Param<T5>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>, WrapParam<T5>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4>,
                       T5 : Enum<T5>, T5 : Param<T5> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(WrapParam(param1), WrapParam(param2), WrapParam(param3), WrapParam(param4), WrapParam(param5)))
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4, T5, T6> Param<T>.ifPresentThen6(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        param5: Param<T5>,
        param6: Param<T6>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>, WrapParam<T5>, WrapParam<T6>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4>,
                       T5 : Enum<T5>, T5 : Param<T5>,
                       T6 : Enum<T6>, T6 : Param<T6> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(WrapParam(param1), WrapParam(param2), WrapParam(param3), WrapParam(param4), WrapParam(param5), WrapParam(param6)))
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4, T5, T6, T7> Param<T>.ifPresentThen7(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        param5: Param<T5>,
        param6: Param<T6>,
        param7: Param<T7>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>, WrapParam<T5>, WrapParam<T6>, WrapParam<T7>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4>,
                       T5 : Enum<T5>, T5 : Param<T5>,
                       T6 : Enum<T6>, T6 : Param<T6>,
                       T7 : Enum<T7>, T7 : Param<T7> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(staticPart = thenParamValue(WrapParam(param1), WrapParam(param2), WrapParam(param3), WrapParam(param4), WrapParam(param5), WrapParam(param6), WrapParam(param7)))
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4, T5, T6, T7, T8> Param<T>.ifPresentThen8(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        param5: Param<T5>,
        param6: Param<T6>,
        param7: Param<T7>,
        param8: Param<T8>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>, WrapParam<T5>, WrapParam<T6>, WrapParam<T7>, WrapParam<T8>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4>,
                       T5 : Enum<T5>, T5 : Param<T5>,
                       T6 : Enum<T6>, T6 : Param<T6>,
                       T7 : Enum<T7>, T7 : Param<T7>,
                       T8 : Enum<T8>, T8 : Param<T8> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(
                staticPart = thenParamValue(
                    WrapParam(param1),
                    WrapParam(param2),
                    WrapParam(param3),
                    WrapParam(param4),
                    WrapParam(param5),
                    WrapParam(param6),
                    WrapParam(param7),
                    WrapParam(param8)
                )
            )
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9> Param<T>.ifPresentThen9(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        param5: Param<T5>,
        param6: Param<T6>,
        param7: Param<T7>,
        param8: Param<T8>,
        param9: Param<T9>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>, WrapParam<T5>, WrapParam<T6>, WrapParam<T7>, WrapParam<T8>, WrapParam<T9>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4>,
                       T5 : Enum<T5>, T5 : Param<T5>,
                       T6 : Enum<T6>, T6 : Param<T6>,
                       T7 : Enum<T7>, T7 : Param<T7>,
                       T8 : Enum<T8>, T8 : Param<T8>,
                       T9 : Enum<T9>, T9 : Param<T9> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(
                staticPart = thenParamValue(
                    WrapParam(param1),
                    WrapParam(param2),
                    WrapParam(param3),
                    WrapParam(param4),
                    WrapParam(param5),
                    WrapParam(param6),
                    WrapParam(param7),
                    WrapParam(param8),
                    WrapParam(param9)
                )
            )
        )

    context(_: Raise<DomainError>)
    fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Param<T>.ifPresentThen10(
        param1: Param<T1>,
        param2: Param<T2>,
        param3: Param<T3>,
        param4: Param<T4>,
        param5: Param<T5>,
        param6: Param<T6>,
        param7: Param<T7>,
        param8: Param<T8>,
        param9: Param<T9>,
        param10: Param<T10>,
        thenParamValue: (WrapParam<T1>, WrapParam<T2>, WrapParam<T3>, WrapParam<T4>, WrapParam<T5>, WrapParam<T6>, WrapParam<T7>, WrapParam<T8>, WrapParam<T9>, WrapParam<T10>) -> String
    ): IfPresent where T : Enum<T>, T : Param<T>,
                       T1 : Enum<T1>, T1 : Param<T1>,
                       T2 : Enum<T2>, T2 : Param<T2>,
                       T3 : Enum<T3>, T3 : Param<T3>,
                       T4 : Enum<T4>, T4 : Param<T4>,
                       T5 : Enum<T5>, T5 : Param<T5>,
                       T6 : Enum<T6>, T6 : Param<T6>,
                       T7 : Enum<T7>, T7 : Param<T7>,
                       T8 : Enum<T8>, T8 : Param<T8>,
                       T9 : Enum<T9>, T9 : Param<T9>,
                       T10 : Enum<T10>, T10 : Param<T10> =
        builder.optional(
            parameterName = parameterName(),
            section = Section(
                staticPart = thenParamValue(
                    WrapParam(param1),
                    WrapParam(param2),
                    WrapParam(param3),
                    WrapParam(param4),
                    WrapParam(param5),
                    WrapParam(param6),
                    WrapParam(param7),
                    WrapParam(param8),
                    WrapParam(param9),
                    WrapParam(param10)
                )
            )
        )



    context(_: Raise<DomainError>)
    infix fun Param<*>.ifTrueThen(thenParamValue: Param<*>) {
        builder.conditional(
            parameterName = parameterName(),
            section = Section(thenParamValue.dynamic)
        )
    }
    context(_: Raise<DomainError>)
    infix fun Param<*>.ifTrueThen(thenValue: String) {
        builder.conditional(
            parameterName = parameterName(),
            section = Section(thenValue)
        )
    }

    context(_: Raise<DomainError>)
    fun Param<*>.ifTrueThen(
        param: Param<*>,
        predicate: (Param<*>)->Boolean,
        value: String
    ) {
        if(predicate(param))
            builder.conditional(
                parameterName = parameterName(),
                section = Section(value)
            )
    }




}