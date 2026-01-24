package com.niloda.cot.domain.dsl.builders

import com.niloda.cot.domain.template.Configurable


class ConcreteCotBuilder(
    override val configurables: MutableList<Configurable> = mutableListOf(),
    // collects top-level, always-included content
    override val unconditional: SectionBuilder = SectionBuilder()
): CotBuilder
