package com.niloda.cot

import arrow.core.getOrElse
import arrow.core.handleErrorWith
import com.niloda.cot.Params.*
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.generate.renderConfigurable
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section.Part.Dynamic

interface Param<T> where T : Param<T> {
    fun parameterName(): String = (this as Enum<*>).name
    val dynamic: Dynamic get() = Dynamic(parameterName())
}

enum class Domain2 : Param<Domain2> {
    a,b,c;
}
enum class Params : Param<Params> {
    x,
    y,
    NAME,
    q,
    z;
}

fun <T: Param<T>> param(param: Param<T>) = "{{$param}}"



fun main() {
    val it = cot("Template") {
        "formatted {{NAME}} {{x}} {{a}}".valid(Params.entries + Domain2.entries)
    }.getOrElse { it: DomainError -> throw Exception("Error $it") }

    println(
        generate(
            cot = it,
            params = RenderParams.of(
                mapOf(
                    "z" to true,
                    "x" to true,
                    "y" to false,
                    "q" to "forgot q",
                    "NAME" to "World"
                )
            ),
            render = { r,c, p -> renderConfigurable(r, c, p)?.let { it + "\n" } }
        )
    )

}