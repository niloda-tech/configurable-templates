package com.example.library

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.Raise

object UserValidator {
    fun validateUser(name: String, age: Int): Either<List<ValidationError>, User> = either {
        val validationErrors = mutableListOf<ValidationError>()

        if (name.isBlank()) {
            validationErrors.add(ValidationError.InvalidName)
        }

        if (age < 18) {
            validationErrors.add(ValidationError.InvalidAge)
        }

        if (validationErrors.isNotEmpty()) {
            raise(validationErrors)
        }

        User(name, age)
    }
}
