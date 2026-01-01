package com.niloda.cot.domain.generate

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.template.Configurable
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.Section

/**
 * Generate the rendered output for the given [cot] using [params].
 */
fun generate(cot: Cot, params: RenderParams): Either<DomainError, String> = either {
    val sb = StringBuilder()
    for (cfg in cot.schema) {
        val rendered = renderConfigurable(this, cfg, params)
        if (rendered != null) sb.append(rendered)
    }
    sb.toString()
}

private fun renderConfigurable(R: Raise<DomainError>, cfg: Configurable, params: RenderParams): String? = with(R) {
    when (cfg) {
        is Configurable.Unconditional -> {
            renderSection(R, cfg.section, params)
        }
        is Configurable.Conditional -> {
            val include = paramBoolean(R, params, cfg.parameterName)
            if (include) renderSection(R, cfg.section, params) else null
        }
        is Configurable.Repetition -> {
            val count = paramInt(R, params, cfg.parameterName)
            ensure(count >= 0) { DomainError.RepeatError("negative repeat count: $count") }
            val sb = StringBuilder()
            repeat(count) { sb.append(renderSection(R, cfg.section, params)) }
            sb.toString()
        }
        is Configurable.OneOf -> {
            val key = paramString(R, params, cfg.parameterName)
            val section = cfg.choices[key]
            ensureNotNull(section) { DomainError.InvalidChoiceKey(key) }
            renderSection(R, section, params)
        }
    }
}

private fun renderSection(R: Raise<DomainError>, section: Section, params: RenderParams): String = with(R) {
    val sb = StringBuilder()
    section.parts.forEach { part ->
        when (part) {
            is Section.Part.Static -> sb.append(part.content)
            is Section.Part.Dynamic -> sb.append(resolveDynamic(R, part.parameterName, params))
        }
    }
    sb.toString()
}

private fun resolveDynamic(R: Raise<DomainError>, name: String, params: RenderParams): String = with(R) {
    val v = ensureNotNull(params.get(name)) { DomainError.MissingParameter(name) }
    when (v) {
        is String -> v
        is Number -> v.toString()
        is Boolean -> v.toString()
        else -> raiseType(R, name, expected = "String|Number|Boolean", actual = v::class.simpleName ?: v::class.qualifiedName ?: "unknown")
    }
}

private fun paramBoolean(R: Raise<DomainError>, params: RenderParams, name: String): Boolean = with(R) {
    val v = ensureNotNull(params.get(name)) { DomainError.MissingParameter(name) }
    (v as? Boolean) ?: raiseType(R, name, expected = "Boolean", actual = v::class.simpleName ?: "unknown")
}

private fun paramString(R: Raise<DomainError>, params: RenderParams, name: String): String = with(R) {
    val v = ensureNotNull(params.get(name)) { DomainError.MissingParameter(name) }
    (v as? String) ?: raiseType(R, name, expected = "String", actual = v::class.simpleName ?: "unknown")
}

private fun paramInt(R: Raise<DomainError>, params: RenderParams, name: String): Int = with(R) {
    val v = ensureNotNull(params.get(name)) { DomainError.MissingParameter(name) }
    when (v) {
        is Int -> v
        is Number -> v.toInt()
        else -> raiseType(R, name, expected = "Int", actual = v::class.simpleName ?: "unknown")
    }
}

private fun <A> raiseType(R: Raise<DomainError>, name: String, expected: String, actual: String): A =
    R.raise(DomainError.TypeMismatch(name = name, expected = expected, actual = actual))
