package com.niloda.cot

import com.niloda.cot.Params.*
import com.niloda.cot.domain.dsl.actions.decoratedCot
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.template.Section

interface Param<T> where T : Param<T>, T : Enum<T>
enum class Params : Param<Params> {
    x,
    y,
    NAME,
    z;

}

fun main() {
    val it = decoratedCot("Template") {
        +"Hello"
        +NAME
        +" "
//        x ifTrue "cool"
//        y ifTrue "cool y"
        z ifPresent "anything"
        optional("q", Section("q was present"))
        conditional("x", Section(listOf(Section.Part.Static("ok"))))
    }.getOrNull() ?: throw Exception()
    println(
        generate(
            it, RenderParams.of(
                mapOf(
                    "z" to true,
                    "x" to true,
                    "y" to false,
                    "q" to "forgot q",
                    "NAME" to "World"
                )
            )
        )
    )

}