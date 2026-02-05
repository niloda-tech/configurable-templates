# Copilot Instructions for Configurable Templates (COT)

## Project Overview

This is a Kotlin-based project for creating **Configurable Templates** using a Domain Specific Language (DSL). The project consists of three main modules:

1. **cot-dsl**: Core DSL library for defining configurable templates
2. **cot-simple-endpoints**: Ktor-based REST API backend for managing templates
3. **cot-frontend**: Kobweb-based web editor for creating and editing templates

The project enables users to create type-safe, configurable code templates with parameters, conditionals, repetition, and choices.

## Technology Stack

- **Language**: Kotlin 2.2.21
- **Backend Framework**: Ktor 3.0.1
- **Frontend Framework**: Kobweb 0.23.3
- **Functional Programming**: Arrow-kt 2.2.0 (Raise DSL for error handling)
- **Serialization**: kotlinx.serialization
- **Build Tool**: Gradle 9.2.1
- **Testing**: JUnit, Ktor Test, Jacoco for coverage

## Coding Standards

### Functional Programming Principles

This project strictly follows **functional programming** principles using Arrow-kt:

1. **Never throw exceptions in business logic** - Use `Either<DomainError, T>` for error handling
2. **Prefer immutability** - Use `val` over `var`, immutable data structures
3. **Use expressions over statements** - Write code as expressions wherever possible
4. **Leverage Arrow's Raise DSL** - Use `either { ... }` blocks with `raise()` for typed errors
5. **Explicit error handling** - Function signatures that can fail must return `Either<E, A>`

### Error Handling Rules

**CRITICAL**: Follow these error handling requirements:

- Public functions that can fail **must** return `Either<DomainError, A>`
- **Never** throw exceptions for expected error conditions
- Use `arrow.core.raise.either` and `raise()` for error flow control
- Define domain-specific error types as sealed interfaces/data classes
- Convert external library exceptions to typed errors at boundaries using `Either.catch`
- Use `context(Raise<E>)` functions for composable validations

**Example:**
```kotlin
sealed interface DomainError
data class ValidationError(val msg: String) : DomainError

fun validate(input: String): Either<DomainError, String> = either {
    ensure(input.isNotBlank()) { ValidationError("Input cannot be blank") }
    input
}
```

### Code Style

- Use Arrow's data types: `Either`, `Option`, `Validated`
- Keep computation blocks expression-oriented
- Avoid side effects inside `either`/`option` blocks
- Use expressive names; avoid abbreviations
- Prefer pure functions without side effects
- Use `ensure(condition) { Error }` for validation in Raise contexts

## Architecture

### Module Structure

The project uses **separate modules** for frontend and backend to avoid dependency conflicts:

- **Backend (cot-simple-endpoints)**: Pure JVM module with Ktor, runs on port 8080
- **Frontend (cot-frontend)**: Pure JS module with Kobweb, runs on port 8081
- **DSL (cot-dsl)**: Core library shared by both modules

### API Communication

- Development: Frontend calls `http://localhost:8080/api/*`
- Production: Frontend calls same origin (deployed together)
- CORS enabled on backend for local development

## Building and Testing

### Build Commands

```bash
# Build all modules
gradle build

# Build specific module
gradle :cot-dsl:build
gradle :cot-simple-endpoints:build
gradle :cot-frontend:build

# Run tests
gradle test

# Run tests with coverage
gradle :cot-simple-endpoints:test :cot-simple-endpoints:jacocoTestReport
```

### Running the Application

**Backend:**
```bash
gradle :cot-simple-endpoints:run
# Server starts on http://localhost:8080
```

**Frontend:**
```bash
cd cot-frontend
kobweb run
# Editor available at http://localhost:8081
```

### Testing Requirements

- **Write comprehensive tests** for all new features
- **Maintain high coverage** - Current backend coverage is 49% instruction coverage
- **Test error paths** - Ensure all `Either.Left` cases are tested
- **Repository tests** must verify concurrent safety
- **Integration tests** for all API endpoints

## Documentation

Important documentation files to reference:

- `ARCHITECTURE.md` - Module separation and system architecture
- `.ai/CODING_STYLE.md` - Detailed coding style with Arrow-kt examples
- `.ai/EXCEPTION_HANDLING.md` - Error handling patterns and best practices
- `.ai/CONTRIBUTING.md` - Contribution guidelines
- `README.md` - General project overview and getting started

## Common Patterns to Follow

### Repository Pattern

```kotlin
// Repositories return Either for operations that can fail
interface CotRepository {
    fun findById(id: CotId): Either<DomainError, Cot>
    fun save(cot: Cot): Either<DomainError, Unit>
}
```

### API Routes

```kotlin
// Ktor routes handle Either results
routing {
    post("/api/cots") {
        val result: Either<DomainError, Cot> = createCot(...)
        result.fold(
            ifLeft = { error -> call.respond(HttpStatusCode.BadRequest, error) },
            ifRight = { cot -> call.respond(HttpStatusCode.Created, cot) }
        )
    }
}
```

### DSL Generation

```kotlin
// COT DSL uses type-safe builders
val template = cot("MyTemplate") {
    "Hello, ".text
    Params.name ifTrueThen "World"
    "!".text
}
```

## What to Avoid

1. **Don't throw exceptions** in business logic - Use `Either` instead
2. **Don't use nullable types (`?`)** when `Option` is more appropriate
3. **Don't mix concerns** - Keep frontend and backend modules separate
4. **Don't use `var`** unless absolutely necessary - Prefer immutability
5. **Don't add new dependencies** without considering Arrow-kt alternatives
6. **Don't modify working tests** - Only add new tests for new features
7. **Don't skip error handling** - All failure cases must be modeled in types

## Best Practices

1. **Analyze existing code** before making changes to understand patterns
2. **Start with types** - Define domain errors and success types first
3. **Use `either { }` blocks** for sequential error-prone operations
4. **Test both success and failure paths** thoroughly
5. **Keep functions small** and composable
6. **Document complex logic** with clear comments
7. **Update relevant documentation** when adding features
8. **Follow the repository's existing structure** and naming conventions

## LLM-Specific Guidance

When contributing as an AI coding agent:

- **Study existing patterns** in the codebase before writing new code
- **Match the functional style** - This is not traditional imperative Kotlin
- **Respect the Arrow-kt patterns** - Don't introduce exception-throwing code
- **Test your changes** - Run the test suite before submitting
- **Update documentation** if you add or modify features
- **Ask for clarification** if requirements conflict with functional principles
