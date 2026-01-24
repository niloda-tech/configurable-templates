package com.niloda.cot.domain.dsl.builders

import com.niloda.cot.domain.dsl.actions.LogicalDsl

class DecoratedBuilder(
    override val builder: ConcreteCotBuilder
) : CotBuilder by builder, LogicalDsl