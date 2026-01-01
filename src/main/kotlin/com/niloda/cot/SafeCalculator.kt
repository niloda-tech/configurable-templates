package com.niloda.cot

import arrow.core.Either
import arrow.core.raise.either

object SafeCalculator {
    fun safeDivide(a: Int, b: Int): Either<CalculatorError, Int> = either {
        if (b == 0) {
            raise(CalculatorError.DivisionByZero)
        }
        a / b
    }
}
