package com.niloda.cot

import com.niloda.cot.Params.*
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.template.Section

interface Param<T> where T : Param<T>, T: Enum<T>
enum class Params: Param<Params> {
    x,
    y,
    NAME,
    z;

}
fun main() {
    val it = cot("Template") {
        +"Hello"
        -z
        -"cool" ifParam "x"
        -"y" ifParam x
        -"z" ifParam z
        conditional("x", Section(listOf(Section.Part.Static("ok"))))
    }.getOrNull() ?: throw Exception()
    println(generate(it, RenderParams.of(mapOf("x" to false, "y" to "neat")))
        .getOrNull())
    
}