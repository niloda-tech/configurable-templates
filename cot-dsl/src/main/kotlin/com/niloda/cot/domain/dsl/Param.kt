package com.niloda.cot.domain.dsl

import com.niloda.cot.domain.template.Section

interface Param<T> where T : Param<T> {
    fun parameterName(): String = (this as Enum<*>).name
    val dynamic: Section.Part.Dynamic get() = Section.Part.Dynamic(parameterName())
}