# COT Simple Editor

A web-based editor for creating, editing, and generating output from Configurable Templates (COTs).

## Overview

The COT Simple Editor provides an intuitive interface for working with the COT DSL. It consists of:

- **Backend**: Ktor-based REST API for COT management and generation
- **Frontend**: Kobweb-based web UI for editing and generating templates
- **Storage**: In-memory repository for quick prototyping and development

## Features (Planned)

- ✅ RESTful API for COT CRUD operations
- ✅ Generate output from COTs with parameters
- ✅ Web-based COT editor with syntax highlighting
- ✅ Dynamic parameter form generation
- ✅ Real-time validation feedback
- ✅ Copy-to-clipboard for generated output

## Technology Stack

- **Kotlin 2.2.21**: Programming language
- **Ktor 3.0.1**: Backend web framework
- **Arrow-kt 2.2.0**: Functional programming and typed error handling
- **Kobweb**: Frontend framework for Compose HTML
- **kotlinx.serialization**: JSON serialization

## Project Structure

```
cot-simple-endpoints/
├── build.gradle.kts
├── README.md
├── IMPLEMENTATION_PLAN.md          # Detailed implementation plan
└── src/
    ├── jvmMain/kotlin/              # Backend (Ktor server)
    │   └── com/niloda/cot/simple/
    │       ├── Application.kt
    │       ├── api/                 # REST API routes
    │       ├── repository/          # Data storage
    │       └── service/             # Business logic
    └── jsMain/kotlin/               # Frontend (Kobweb UI)
        └── com/niloda/cot/simple/
            ├── Main.kt
            ├── pages/               # UI pages
            └── components/          # Reusable components
```

## Getting Started

### Prerequisites

- JDK 17 or higher
- Gradle 8.x (included via wrapper)

### Running the Application

#### Development Mode

**Backend**:
```bash
./gradlew :cot-simple-endpoints:run
```

The backend server will start on `http://localhost:8080`

**Frontend** (when implemented):
```bash
./gradlew :cot-simple-endpoints:kobwebStart
```

The frontend will be available on `http://localhost:8081`

### Testing

Run all tests:
```bash
./gradlew :cot-simple-endpoints:test
```

## API Documentation

### Endpoints

#### Health Check
```
GET /health
Response: { "status": "ok" }
```

#### List COTs
```
GET /api/cots
Response: { "cots": [...] }
```

#### Get COT
```
GET /api/cots/{id}
Response: { "id": "...", "name": "...", ... }
```

#### Create COT
```
POST /api/cots
Body: { "name": "...", "dslCode": "..." }
Response: { "id": "...", ... }
```

#### Update COT
```
PUT /api/cots/{id}
Body: { "name": "...", "dslCode": "..." }
Response: { "id": "...", ... }
```

#### Delete COT
```
DELETE /api/cots/{id}
Response: 204 No Content
```

#### Generate Output
```
POST /api/cots/{id}/generate
Body: { "parameters": { "key": "value", ... } }
Response: { "output": "..." }
```

## Example Usage

### Creating a COT

```bash
curl -X POST http://localhost:8080/api/cots \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Greeting",
    "dslCode": "cot(\"Greeting\") { \"Hello, \".text; Params.name ifTrueThen \"World\" }"
  }'
```

### Generating Output

```bash
curl -X POST http://localhost:8080/api/cots/{id}/generate \
  -H "Content-Type: application/json" \
  -d '{
    "parameters": {
      "name": "true"
    }
  }'
```

## Development

### Architecture

The application follows clean architecture principles:

- **Presentation Layer**: Ktor routes + Kobweb UI
- **Domain Layer**: COT business logic (from `cot-dsl` module)
- **Data Layer**: In-memory repository

### Error Handling

Following the project's functional programming guidelines, all operations that can fail return `Either<DomainError, T>` using Arrow-kt's Raise DSL.

Example:
```kotlin
fun createCot(request: CreateCotRequest): Either<DomainError, StoredCot> = either {
    ensure(request.name.isNotBlank()) {
        DomainError.InvalidName("createCot", "Name cannot be blank")
    }
    
    val cot = parseDslCode(request.dslCode).bind()
    repository.create(cot, request.dslCode).bind()
}
```

### Adding New Features

1. Define domain models in `api/models/`
2. Implement business logic using Arrow's Either
3. Add routes in `api/CotRoutes.kt`
4. Create corresponding UI components in `jsMain/`
5. Write tests for new functionality

## Testing

### Unit Tests
Test individual components and business logic:
```kotlin
@Test
fun `repository stores and retrieves COT`() {
    val repo = InMemoryCotRepository()
    val cot = // ... create test COT
    
    val stored = repo.create(cot, "dsl code").getOrNull()!!
    val retrieved = repo.findById(stored.id).getOrNull()!!
    
    assertEquals(stored.id, retrieved.id)
}
```

### Integration Tests
Test API endpoints with Ktor's test utilities:
```kotlin
@Test
fun `GET cots returns list`() = testApplication {
    application {
        module()
    }
    
    client.get("/api/cots").apply {
        assertEquals(HttpStatusCode.OK, status)
    }
}
```

## Deployment

### Production Build

Build the backend:
```bash
./gradlew :cot-simple-endpoints:build
```

Build frontend (when implemented):
```bash
./gradlew :cot-simple-endpoints:kobwebExport
```

### Running in Production

```bash
java -jar cot-simple-endpoints/build/libs/cot-simple-endpoints.jar
```

## Contributing

See the main project's [CONTRIBUTING.md](../.ai/CONTRIBUTING.md) for guidelines.

Key points:
- Follow functional programming style with Arrow-kt
- Use `Either` for error handling, never throw exceptions
- Write tests for all new features
- Follow the coding style in [CODING_STYLE.md](../.ai/CODING_STYLE.md)

## Related Documentation

- [Implementation Plan](IMPLEMENTATION_PLAN.md) - Detailed implementation roadmap
- [Project Coding Style](../.ai/CODING_STYLE.md) - Coding guidelines
- [Exception Handling](../.ai/EXCEPTION_HANDLING.md) - Error handling patterns
- [COT DSL Documentation](../cot-dsl/) - Core DSL module

## License

See [LICENSE](../LICENSE) file in the root directory.

## Status

🚧 **Under Development** - This is a planned feature. See [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md) for the roadmap.

Current implementation status:
- [x] Basic Ktor server setup
- [x] Health check endpoint
- [ ] COT repository
- [ ] CRUD API endpoints
- [ ] Generation endpoint
- [ ] Kobweb frontend
- [ ] COT editor UI
- [ ] Parameter form UI
- [ ] Tests and documentation

## Quick Examples

### Simple Static Template
```kotlin
cot("Static") {
    "Hello, World!".text
}
```

### Conditional Template
```kotlin
cot("Conditional") {
    conditional("showGreeting") {
        section {
            static("Hello, ")
            dynamic("name")
        }
    }
}
```

### Repetition Template
```kotlin
cot("List") {
    repeat("count") {
        section {
            static("- Item\n")
        }
    }
}
```

### One-Of Template
```kotlin
cot("ChoiceTemplate") {
    oneOf("language") {
        choice("en") { static("Hello") }
        choice("es") { static("Hola") }
        choice("fr") { static("Bonjour") }
    }
}
```

## Support

For issues and questions, please refer to the main project repository.
