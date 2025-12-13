# Coding Style Guide

This guide outlines the coding style and patterns to be followed in this project. The goal is to maintain a consistent, readable, and maintainable codebase using functional programming principles.

## Functional Approach with Arrow-kt

This project uses [Arrow-kt](https://arrow-kt.io/) to facilitate a more functional style of programming in Kotlin. When contributing, please adhere to the following principles:

### 1. Prefer Expressions over Statements

Write code as expressions wherever possible. This leads to more concise and readable code.

**Good:**
```kotlin
val result = if (condition) "A" else "B"
```

**Bad:**
```kotlin
val result: String
if (condition) {
    result = "A"
} else {
    result = "B"
}
```

### 2. Immutability

Strive for immutability. Use `val` instead of `var` and prefer immutable data structures. This helps to prevent side effects and makes the code easier to reason about.

### 3. Use Arrow's Data Types

Leverage Arrow's data types for handling common patterns:

-   **`Either<E, A>`**: For computations that can fail. Use `Either` to explicitly model success (`Right<A>`) and failure (`Left<E>`) cases instead of throwing exceptions.
-   **`Option<A>`**: For values that may be absent. Prefer `Option` over nullable types (`?`) to make the absence of a value explicit.
-   **`Validated<E, A>`**: For accumulating errors during validation.

### 4. Expression-Oriented Logic

Use Arrow's computation blocks (`either { ... }`, `option { ... }`) to write sequential logic in a clear, expressive way without deep nesting.
