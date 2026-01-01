package com.niloda.cot

// Minimal types to satisfy example code and tests in this repository.

sealed interface CalculatorError {
    data object DivisionByZero : CalculatorError
}

sealed interface ValidationError {
    data object InvalidName : ValidationError
    data object InvalidAge : ValidationError
}

data class User(val name: String, val age: Int)
