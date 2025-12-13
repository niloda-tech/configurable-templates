package com.example.library

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserValidatorTest {

    @Test
    fun `valid user should be returned`() {
        UserValidator.validateUser("John Doe", 25).fold(
            { throw AssertionError("Should be valid") },
            { user ->
                assertEquals("John Doe", user.name)
                assertEquals(25, user.age)
            }
        )
    }

    @Test
    fun `invalid name should return error`() {
        UserValidator.validateUser("", 25).fold(
            { errors ->
                assertTrue(errors.contains(ValidationError.InvalidName))
            },
            { throw AssertionError("Should be invalid") }
        )
    }

    @Test
    fun `invalid age should return error`() {
        UserValidator.validateUser("John Doe", 17).fold(
            { errors ->
                assertTrue(errors.contains(ValidationError.InvalidAge))
            },
            { throw AssertionError("Should be invalid") }
        )
    }

    @Test
    fun `invalid name and age should return both errors`() {
        UserValidator.validateUser("", 17).fold(
            { errors ->
                assertTrue(errors.contains(ValidationError.InvalidName))
                assertTrue(errors.contains(ValidationError.InvalidAge))
            },
            { throw AssertionError("Should be invalid") }
        )
    }
}
