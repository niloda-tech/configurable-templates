package com.niloda.cot.sandbox.service.sandbox

val Any.escaped get (): String =
    toString()
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        .replace("$", "\\$")