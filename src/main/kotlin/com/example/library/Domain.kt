package com.example.library

data class User(val name: String, val age: Int)

sealed class ValidationError {
    data object InvalidName : ValidationError()
    data object InvalidAge : ValidationError()
}

sealed interface CalculatorError {
    data object DivisionByZero : CalculatorError
}
