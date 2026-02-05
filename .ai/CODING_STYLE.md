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

### 5. Raise DSL — Preferred Error Handling

Leverage Arrow's latest Raise DSL to model typed errors without throwing exceptions.

-   Prefer returning `Either<E, A>` from public APIs that can fail.
-   Implement logic inside `either { ... }` which provides a `Raise<E>` context.
-   Use `raise(e)` to short‑circuit on failures; the last expression becomes the success value.
-   Define reusable validations as **context-parameter functions** (Kotlin 2.2+): `context(_: Raise<E>)`.

Example (typed validation with `Raise`):

```kotlin
sealed interface DomainError
data class EmptyName(val msg: String) : DomainError
data class InvalidChar(val c: Char) : DomainError

context(_: Raise<DomainError>)
fun validateName(name: String): String {
    ensure(name.isNotBlank()) { EmptyName("Name is empty") }
    ensure(name.all { it.isLetter() }) { InvalidChar(name.first { !it.isLetter() }) }
    return name
}

fun greet(name: String): Either<DomainError, String> = either {
    val n = validateName(name)
    "Hello, $n"
}
```

Key helpers to use in `Raise` context:

-   `ensure(condition) { E }` — raise `E` when the condition is false.
-   `ensureNotNull(value) { E }` — raise if `value` is null.
-   `recover { ... }` — handle/transform raised errors within the same context when appropriate.

### 6. Converting Exceptions at Boundaries

Convert thrown exceptions from external libraries into typed errors at the boundary using `Either.catch` (or `either { ... }` with `catch`), then map to your domain error:

```kotlin
data class NotANumber(val input: String) : DomainError

fun safeParseInt(s: String): Either<DomainError, Int> = either {
    Either.catch { s.toInt() }
        .mapLeft { NotANumber(s) }
        .bind()
}
```

### 7. Accumulating Errors (Validated)

When you need to validate many fields and report all errors, use `Validated` with a non‑empty collection (e.g., `NonEmptyList`) and `zipOrAccumulate`:

```kotlin
fun validateA(a: String): ValidatedNel<DomainError, String> =
    if (a.isNotBlank()) a.validNel() else EmptyName("a").invalidNel()

fun validateB(b: String): ValidatedNel<DomainError, String> =
    if (b.all { it.isLetter() }) b.validNel() else InvalidChar(b.first { !it.isLetter() }).invalidNel()

val combined: ValidatedNel<DomainError, Pair<String, String>> =
    zipOrAccumulate(validateA("foo"), validateB("bar")) { va, vb -> va to vb }
```

Prefer `ValidatedNel<E, A>` when error accumulation is required; otherwise default to `Either<E, A>` with `Raise`.

### 8. Options and Nullability

-   Prefer `Option<A>` over nullable types for explicit absence.
-   Use `option { ... }` when building values that can be absent, and `ensure(...)` inside to enforce preconditions.
-   Interop helpers: `nullable?.toOption()`, `option.getOrElse { ... }`.

```kotlin
fun firstLetterIfAlpha(s: String): Option<Char> = option {
    ensure(s.isNotEmpty()) { }
    val c = s.first()
    ensure(c.isLetter()) { }
    c
}
```

### 9. API Conventions for This Project

-   Public functions that can fail should return `Either<DomainError, A>` and never throw for expected conditions.
-   Provide internal `context(_: Raise<DomainError>)` helpers for validations and small building blocks.
-   Keep data immutable; avoid `var` and prefer pure functions.
-   Use expressive names; avoid abbreviations unless widely accepted.
-   Keep computation blocks expression‑oriented; avoid side effects inside `either`/`option`.
