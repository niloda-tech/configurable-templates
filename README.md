# Configurable Templates (COT)

A powerful DSL for creating configurable code templates with a web-based editor.

## Overview

This library provides a Domain Specific Language (DSL) for creating Configurable Templates (COTs) that can be used to generate code and other text-based content. The project includes:

- **COT DSL**: A Kotlin-based DSL for defining configurable templates
- **REST API**: Ktor-based backend for managing templates
- **Web Editor**: Kobweb-based frontend for creating and editing templates

## Project Structure

```
configurable-templates/
├── cot-dsl/                    # Core DSL library
│   └── src/main/kotlin/        # Domain models and DSL implementation
├── cot-simple-endpoints/       # REST API backend
│   ├── src/main/kotlin/        # Ktor server and API routes
│   └── src/test/kotlin/        # Comprehensive test suite
└── cot-frontend/               # Web-based editor
    └── src/jsMain/kotlin/      # Kobweb UI components
```

## Features

### Core DSL (`cot-dsl`)
- **Type-safe template definition**: Define templates using Kotlin DSL
- **Configurable items**: Support for conditionals, repetition, and choices
- **Parameter validation**: Type-checked parameters with default values
- **Code generation**: Render templates with provided parameters

### REST API (`cot-simple-endpoints`)
- **CRUD operations**: Create, read, update, and delete templates
- **Template generation**: Generate output from templates with parameters
- **Error handling**: Type-safe error handling using Arrow-kt
- **Concurrent-safe storage**: In-memory repository with ConcurrentHashMap

### Web Editor (`cot-frontend`)
- **Template management**: Browse and manage your templates
- **Live editing**: Create and edit templates with immediate feedback
- **Parameter input**: Dynamic forms based on template schema
- **Output generation**: Generate and preview template output

## Getting Started

### Prerequisites

- **JDK 17 or higher**
- **Gradle 8.x** (included via wrapper)

### Building the Project

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :cot-dsl:build
./gradlew :cot-simple-endpoints:build
./gradlew :cot-frontend:build
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew :cot-simple-endpoints:test :cot-simple-endpoints:jacocoTestReport

# View coverage report
open cot-simple-endpoints/build/reports/jacoco/test/html/index.html
```

### Running the Application

**Backend API**:
```bash
./gradlew :cot-simple-endpoints:run
```

The API server will start on `http://localhost:8080`

**Frontend** (separate terminal):
```bash
cd cot-frontend
kobweb run
```

The web editor will be available at `http://localhost:8081`

## Quick Example

### Creating a Template with the DSL

```kotlin
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.generate.RenderParams

// Define a configurable template
val greetingTemplate = cot("Greeting") {
    "Hello, ".text
    Params.name ifTrueThen "World"
    "!".text
}

// Generate output with parameters
val params = RenderParams.of(mapOf("name" to "Alice"))
val output = greetingTemplate.generate(params)
// Output: "Hello, Alice!"
```

### Using the REST API

```bash
# Create a template
curl -X POST http://localhost:8080/api/cots \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Greeting",
    "dslCode": "cot(\"Greeting\") { \"Hello, \".text; Params.name ifTrueThen \"World\"; \"!\".text }"
  }'

# List all templates
curl http://localhost:8080/api/cots

# Generate output
curl -X POST http://localhost:8080/api/cots/{id}/generate \
  -H "Content-Type: application/json" \
  -d '{
    "parameters": {
      "name": "Alice"
    }
  }'
```

## Documentation

- **[COT DSL Documentation](cot-dsl/README.md)** - Core DSL reference
- **[API Documentation](cot-simple-endpoints/API_DOCUMENTATION.md)** - REST API reference
- **[Frontend Documentation](cot-frontend/README.md)** - Web editor guide
- **[Implementation Plan](cot-simple-endpoints/IMPLEMENTATION_PLAN.md)** - Development roadmap
- **[Architecture](ARCHITECTURE.md)** - System architecture overview

## Development

### Code Style

This project follows functional programming principles using Arrow-kt:

- Use `Either<DomainError, T>` for error handling
- Never throw exceptions in business logic
- Prefer immutable data structures
- Write comprehensive tests for all features

See [CODING_STYLE.md](.ai/CODING_STYLE.md) for detailed guidelines.

### Testing

The project maintains high test coverage standards:

- **Backend**: 49% instruction coverage with 49 test cases
- **Repository**: 100% coverage with concurrent safety tests
- **API**: Integration tests for all endpoints

### Contributing

1. Follow the coding style guidelines
2. Write tests for all new features
3. Update documentation as needed
4. Use Arrow-kt for error handling
5. Never throw exceptions in business logic

See [CONTRIBUTING.md](.ai/CONTRIBUTING.md) for more details.

## Technology Stack

- **Language**: Kotlin 2.2.21
- **Backend**: Ktor 3.0.1
- **Frontend**: Kobweb 0.23.3
- **FP Library**: Arrow-kt 2.2.0
- **Serialization**: kotlinx.serialization
- **Build**: Gradle 9.2.1

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues, questions, or contributions, please refer to the repository's issue tracker.

## Related Projects

- [Arrow-kt](https://arrow-kt.io/) - Functional programming library for Kotlin
- [Ktor](https://ktor.io/) - Asynchronous web framework
- [Kobweb](https://kobweb.varabyte.com/) - Compose HTML web framework

## LLM Guidance

This project includes guidance for Large Language Models (LLMs) on how to contribute to the codebase. See the following files for more information:

-   [`CODING_STYLE.md`](.ai/CODING_STYLE.md)
-   [`EXCEPTION_HANDLING.md`](.ai/EXCEPTION_HANDLING.md)
-   [`CONTRIBUTING.md`](.ai/CONTRIBUTING.md)