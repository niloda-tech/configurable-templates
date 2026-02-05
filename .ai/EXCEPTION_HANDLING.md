# Exception Handling Guide

This project takes a specific approach to exception handling, favoring explicit, type-safe error management over traditional `try-catch` blocks. We use [Arrow's Raise DSL](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/) to achieve this.

## The Rule: Don't Throw Exceptions

As a general rule, functions should not throw exceptions to signal expected errors. Instead, they should return a type that represents the possibility of failure. This makes the error-handling explicit in the function's signature.

## The Tool: `Raise<E>` and `either { ... }`

We use `arrow.core.raise.Raise<E>` and the `either` builder as the primary tools for error handling.

A function that can fail will typically return an `Either<E, A>`:

-   `E` is the error type (the "Left" side).
-   `A` is the success type (the "Right" side).

Inside the function, we operate within a `Raise` context, which allows us to short-circuit the execution when an error occurs.

**Example:**

```kotlin
// Define potential errors
sealed interface GreeterError
data class EmptyName(val message: String) : GreeterError
data class InvalidChar(val char: Char) : GreeterError

fun greet(name: String): Either<GreeterError, String> = either {
    ensure(name.isNotEmpty()) { EmptyName("Name is empty") }
    ensure(name.all { it.isLetter() }) { InvalidChar(name.first { !it.isLetter() }) }
    "Hello, $name"
}
```

In this example:
- The `either` block provides a `Raise<GreeterError>` context.
- `raise(...)` is used to immediately stop execution and return a `Left` value of the corresponding error.
- If no error is raised, the last expression is returned as a `Right` value.

### Handling Exceptions from External Code

When interacting with libraries or APIs that *do* throw exceptions, your first responsibility is to "catch" these exceptions at the boundary and convert them into one of our defined error types.

Use `Either.catch` and then map the error.

**Example:**

```kotlin
sealed interface ParseError
data class NotANumber(val input: String) : ParseError

fun safeParse(s: String): Either<ParseError, Int> = either {
    Either.catch { s.toInt() }
        .mapLeft { NotANumber(s) }
        .bind()
}
```

By doing this, you translate the unsafe, exception-throwing world into the safe, explicit world of our typed errors.

### Composable, Type-Safe Functions

A powerful pattern is to define functions directly on a `Raise` context parameter. This makes them automatically composable.

```kotlin
context(_: Raise<GreeterError>)
fun validatedName(name: String): String {
    ensure(name.isNotEmpty()) { EmptyName("Name is empty") }
    ensure(name.all { it.isLetter() }) { InvalidChar(name.first { !it.isLetter() }) }
    return name
}

fun greet(name: String): Either<GreeterError, String> = either {
    val validated = validatedName(name)
    "Hello, $validated"
}
```

### Benefits of this Approach

1.  **Explicitness**: Function signatures that return `Either` tell the whole story. Callers know they must handle the failure case.
2.  **Type Safety**: The compiler forces you to handle errors. An `Either<E, A>` cannot be used as an `A` until you have explicitly handled the `E` case.
3.  **Readability**: The `raise` DSL allows for writing code that looks almost sequential, as if we are not dealing with errors at all. The error handling is implicit in the `Raise` context.
4.  **Composability**: Functions defined on a `Raise` context can be easily composed together within an `either` block.
