package com.niloda.cot.domain.dsl

import com.niloda.cot.domain.dsl.Param

fun <T: Param<T>> escape(param: Param<T>) = "{{$param}}"