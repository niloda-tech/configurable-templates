package com.niloda.cot.domain.generate

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.template.Configurable
import com.niloda.cot.domain.template.Configurable.Conditional
import com.niloda.cot.domain.template.Configurable.IfPresent
import com.niloda.cot.domain.template.Configurable.Repetition
import com.niloda.cot.domain.template.Configurable.Unconditional
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.domain.template.DomainError.MissingParameter
import com.niloda.cot.domain.template.Section

/**
 * Generate the rendered output for the given [cot] using [params].
 */
fun generate(
    cot: Cot,
    params: RenderParams,
    render: (r: Raise<DomainError>, cfg: Configurable, params: RenderParams) -> String?)
    : Either<DomainError, String> = either {
        val sb = StringBuilder()

        for (cfg in cot.schema) {
            val rendered = render(this, cfg, params)
            if (rendered != null) sb.append(rendered)
        }
        sb.toString()
    }

    fun renderConfigurable(r: Raise<DomainError>, cfg: Configurable, params: RenderParams): String? = with(r) {
    when (cfg) {
        is Unconditional -> renderUnconditional(r = r, params = params, cfg = cfg)
        is Conditional -> renderConditional(r = r, params = params, cfg = cfg)
        is IfPresent -> renderIfPresent(r = r, params = params, cfg = cfg)
        is Repetition -> renderRepetition(r = r, params = params, cfg = cfg)

        is Configurable.OneOf -> {
            val key = paramString(r, params, cfg.parameterName)
            val section = cfg.choices[key]
            ensureNotNull(section) { DomainError.InvalidChoiceKey(key) }
            renderSection(r = r, section = section, params = params)
        }
    }
}

private fun renderConditional(
    r: Raise<DomainError>,
    params: RenderParams,
    cfg: Conditional
): String? {
    val include = paramBoolean(
        r = r,
        params = params,
        name = cfg.parameterName
    )
    return if (include) renderSection(
        r = r,
        section = cfg.section,
        params = params
    ) else null
}

private fun Raise<DomainError>.renderRepetition(
    r: Raise<DomainError>,
    params: RenderParams,
    cfg: Repetition
): String {
    val count = paramInt(
        r = r,
        params = params,
        name = cfg.parameterName
    )
    ensure(count >= 0) {
        DomainError.RepeatError("negative repeat count: $count")
    }
    val sb = StringBuilder()
    repeat(count) {
        sb.append(
            renderSection(
                r = r,
                section = cfg.section,
                params = params
            )
        )
    }
    return sb.toString()
}

private fun renderIfPresent(
    r: Raise<DomainError>,
    params: RenderParams,
    cfg: IfPresent
): String? {
    val include = paramOptional(
        params = params,
        name = cfg.parameterName
    )
    return if (include) renderUnconditional(
        r = r,
        params = params,
        cfg = Unconditional(cfg.section)
    ) else null
}

private fun renderUnconditional(r: Raise<DomainError>, params: RenderParams, cfg: Unconditional): String =
    with(r) {
        val sb = StringBuilder()
        cfg.section.parts.forEach { part ->
            when (part) {
                is Section.Part.Static -> sb.append(part.content)
                is Section.Part.Dynamic -> sb.append(resolveDynamic(r, part.parameterName, params))
            }
        }
        sb.toString()
    }

private fun renderSection(r: Raise<DomainError>, section: Section, params: RenderParams): String = with(r) {
    val sb = StringBuilder()
    section.parts.forEach { part ->
        when (part) {
            is Section.Part.Static -> sb.append(part.content)
            is Section.Part.Dynamic -> sb.append(resolveDynamic(r, part.parameterName, params))
        }
    }
    sb.toString()
}

private fun resolveDynamic(R: Raise<DomainError>, name: String, params: RenderParams): String = with(R) {
    val v = ensureNotNull(params.get(name)) { MissingParameter(name) }
    when (v) {
        is String -> v
        is Number -> v.toString()
        is Boolean -> v.toString()
        else -> raiseType(
            R,
            name,
            expected = "String|Number|Boolean",
            actual = v::class.simpleName ?: v::class.qualifiedName ?: "unknown"
        )
    }
}


private fun paramBoolean(r: Raise<DomainError>, params: RenderParams, name: String): Boolean = with(r) {
    val v = ensureNotNull(value = params.get(name)) {
        MissingParameter(name)
    }
    (v as? Boolean) ?: raiseType(r, name, expected = "Boolean", actual = v::class.simpleName ?: "unknown")
}

private fun paramString(R: Raise<DomainError>, params: RenderParams, name: String): String = with(R) {
    val v = ensureNotNull(value = params.get(name)) {
        MissingParameter(name)
    }
    (v as? String) ?: raiseType(
        r = R,
        name = name,
        expected = "String",
        actual = v::class.simpleName ?: "unknown"
    )
}

private fun paramInt(r: Raise<DomainError>, params: RenderParams, name: String): Int = with(r) {
    val v = ensureNotNull(params.get(name)) { MissingParameter(name) }
    when (v) {
        is Int -> v
        is Number -> v.toInt()
        else -> raiseType(
            r = r,
            name = name,
            expected = "Int",
            actual = v::class.simpleName ?: "unknown"
        )
    }
}

private fun paramOptional(params: RenderParams, name: String): Boolean =
    params.get(name) != null

private fun <A> raiseType(r: Raise<DomainError>, name: String, expected: String, actual: String): A =
    r.raise(
        r = DomainError.TypeMismatch(
            name = name,
            expected = expected,
            actual = actual
        )
    )
