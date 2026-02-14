package com.niloda.cot

import arrow.core.getOrElse
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.generate.renderConfigurable
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.sample.SampleParams2
import com.niloda.cot.sample.SampleParams


fun main() {
    val it = cot("Template") {
        "formatted {{NAME}} {{x}} {{a}}".valid(SampleParams.entries + SampleParams2.entries)
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