package com.niloda.cot.domain.dsl.builders

import com.niloda.cot.domain.dsl.actions.LogicalDsl
import com.niloda.cot.domain.dsl.actions.OperatorDsl
import com.niloda.cot.domain.dsl.actions.ParamsDsl

class DecoratedBuilder(
    override val builder: ConcreteCotBuilder
) : CotBuilder by builder, OperatorDsl, ParamsDsl, LogicalDsl