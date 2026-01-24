# COT Simple Editor - Implementation Plan

## Overview

This document outlines the implementation plan for building a simple COT (Configurable Templates) editor using Kotlin, Ktor, Arrow-kt, and Kobweb. The editor will provide a web-based interface for creating, editing, and generating output from configurable templates.

## Technology Stack

### Backend
- **Kotlin 2.2.21**: Primary programming language
- **Ktor 3.0.1**: Web framework for REST API
- **Arrow-kt 2.2.0**: Functional programming library for typed error handling
- **kotlinx.serialization**: JSON serialization/deserialization
- **SLF4J**: Logging framework

### Frontend
- **Kobweb**: Compose HTML-based web framework (https://kobweb.varabyte.com/)
- **Kotlin/JS**: Kotlin compiled to JavaScript
- **Compose HTML**: Declarative UI framework

### Data Layer
- **In-Memory Repository**: Simple concurrent-safe map for storing COTs

### Integration
- **cot-dsl module**: Existing DSL for COT definition and code generation

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Browser (Client)                     │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │           Kobweb Frontend (Kotlin/JS)              │ │
│  │  - COT List View                                   │ │
│  │  - COT Editor (Create/Edit)                        │ │
│  │  - Generation Interface                            │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                         │
                         │ HTTP/JSON (REST API)
                         ▼
┌─────────────────────────────────────────────────────────┐
│              Ktor Backend Server (Kotlin/JVM)            │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │                 REST API Routes                    │ │
│  │  - /api/cots (CRUD endpoints)                      │ │
│  │  - /api/cots/{id}/generate                         │ │
│  └────────────────────────────────────────────────────┘ │
│                         │                                │
│                         ▼                                │
│  ┌────────────────────────────────────────────────────┐ │
│  │              Business Logic Layer                  │ │
│  │  - Request validation                              │ │
│  │  - COT construction from DSL                       │ │
│  │  - Generation using cot-dsl module                 │ │
│  └────────────────────────────────────────────────────┘ │
│                         │                                │
│                         ▼                                │
│  ┌────────────────────────────────────────────────────┐ │
│  │           In-Memory COT Repository                 │ │
│  │  - Concurrent-safe storage                         │ │
│  │  - CRUD operations                                 │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                         │
                         │ Uses
                         ▼
           ┌──────────────────────────────┐
           │     cot-dsl Module           │
           │  - COT DSL functions         │
           │  - generate() function       │
           │  - Domain models             │
           └──────────────────────────────┘
```

### Module Structure

```
cot-simple-endpoints/
├── build.gradle.kts
├── IMPLEMENTATION_PLAN.md (this file)
└── src/
    ├── jvmMain/kotlin/com/niloda/cot/simple/
    │   ├── Application.kt              # Ktor server entry point
    │   ├── api/
    │   │   ├── CotRoutes.kt            # REST API routes
    │   │   ├── models/
    │   │   │   ├── CotRequest.kt       # API request models
    │   │   │   └── CotResponse.kt      # API response models
    │   │   └── ErrorResponse.kt        # Error response models
    │   ├── repository/
    │   │   ├── CotRepository.kt        # Repository interface
    │   │   └── InMemoryCotRepository.kt # In-memory implementation
    │   └── service/
    │       └── CotService.kt           # Business logic
    └── jsMain/kotlin/com/niloda/cot/simple/
        ├── Main.kt                     # Kobweb entry point
        ├── pages/
        │   ├── Index.kt                # Home/List page
        │   ├── Create.kt               # Create COT page
        │   ├── Edit.kt                 # Edit COT page
        │   └── Generate.kt             # Generate output page
        └── components/
            ├── CotList.kt              # COT list component
            ├── CotEditor.kt            # COT editor component
            └── ParameterForm.kt        # Parameter input form
```

## API Design

### Endpoints

#### 1. List All COTs
```
GET /api/cots
Response: 200 OK
{
  "cots": [
    {
      "id": "uuid-1",
      "name": "MyTemplate",
      "createdAt": "2026-01-24T17:52:59.704Z",
      "updatedAt": "2026-01-24T17:52:59.704Z"
    }
  ]
}
```

#### 2. Get Single COT
```
GET /api/cots/{id}
Response: 200 OK
{
  "id": "uuid-1",
  "name": "MyTemplate",
  "schema": [ /* Configurable items */ ],
  "createdAt": "2026-01-24T17:52:59.704Z",
  "updatedAt": "2026-01-24T17:52:59.704Z"
}

Error: 404 Not Found
{
  "error": "CotNotFound",
  "message": "COT with id 'uuid-1' not found"
}
```

#### 3. Create COT
```
POST /api/cots
Request:
{
  "name": "MyTemplate",
  "dslCode": "cot(\"MyTemplate\") { ... }"
}

Response: 201 Created
{
  "id": "uuid-1",
  "name": "MyTemplate",
  "schema": [ /* Configurable items */ ],
  "createdAt": "2026-01-24T17:52:59.704Z",
  "updatedAt": "2026-01-24T17:52:59.704Z"
}

Error: 400 Bad Request
{
  "error": "InvalidDsl",
  "message": "Invalid DSL: Name cannot be empty"
}
```

#### 4. Update COT
```
PUT /api/cots/{id}
Request:
{
  "name": "MyTemplateV2",
  "dslCode": "cot(\"MyTemplateV2\") { ... }"
}

Response: 200 OK
{
  "id": "uuid-1",
  "name": "MyTemplateV2",
  "schema": [ /* Configurable items */ ],
  "createdAt": "2026-01-24T17:52:59.704Z",
  "updatedAt": "2026-01-24T18:00:00.000Z"
}
```

#### 5. Delete COT
```
DELETE /api/cots/{id}
Response: 204 No Content

Error: 404 Not Found
```

#### 6. Generate Output
```
POST /api/cots/{id}/generate
Request:
{
  "parameters": {
    "name": "John",
    "enabled": true,
    "count": 3
  }
}

Response: 200 OK
{
  "output": "Generated template content..."
}

Error: 400 Bad Request
{
  "error": "GenerationError",
  "message": "Missing required parameter: name"
}
```

## Data Models

### Backend Models

```kotlin
// Storage model
data class StoredCot(
    val id: String,
    val cot: Cot,
    val dslCode: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

// API Request models
@Serializable
data class CreateCotRequest(
    val name: String,
    val dslCode: String
)

@Serializable
data class UpdateCotRequest(
    val name: String,
    val dslCode: String
)

@Serializable
data class GenerateRequest(
    val parameters: Map<String, JsonElement>
)

// API Response models
@Serializable
data class CotListResponse(
    val cots: List<CotSummary>
)

@Serializable
data class CotSummary(
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CotDetailResponse(
    val id: String,
    val name: String,
    val dslCode: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class GenerateResponse(
    val output: String
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
```

## Implementation Phases

### Phase 1: Backend Foundation
**Goal**: Set up Ktor server with basic CRUD endpoints

**Tasks**:
1. Update `build.gradle.kts` to add Arrow dependencies
2. Add dependency on `:cot-dsl` module
3. Implement `InMemoryCotRepository` with concurrent-safe storage
4. Create API request/response models with kotlinx.serialization
5. Implement Ktor routes for CRUD operations
6. Add error handling using Arrow's Either
7. Test endpoints with curl/HTTP client

**Acceptance Criteria**:
- All CRUD endpoints return proper HTTP status codes
- Repository handles concurrent access safely
- Errors are returned as typed DomainError converted to ErrorResponse
- Can create, read, update, delete COTs via API

### Phase 2: Generation Endpoint
**Goal**: Implement COT generation with parameter validation

**Tasks**:
1. Create `GenerateRequest` model with parameter map
2. Implement parameter parsing from JSON to RenderParams
3. Add `/api/cots/{id}/generate` endpoint
4. Integrate with `generate()` function from cot-dsl
5. Handle generation errors and map to error responses
6. Test generation with various parameter combinations

**Acceptance Criteria**:
- Can generate output from stored COT with parameters
- Missing parameters return clear error messages
- Type mismatches are caught and reported
- Generated output matches expected format

### Phase 3: Kobweb Frontend Setup
**Goal**: Initialize Kobweb project and basic layout

**Tasks**:
1. Add Kobweb Gradle plugin to build.gradle.kts
2. Configure multiplatform build (jvm + js targets)
3. Create basic Kobweb site structure
4. Implement base layout with navigation
5. Setup routing for main pages
6. Configure API client for backend communication
7. Test basic page rendering

**Acceptance Criteria**:
- Kobweb dev server starts successfully
- Can navigate between pages
- API client can communicate with Ktor backend
- Basic styling is applied

### Phase 4: COT List & Detail Views
**Goal**: Display and navigate COTs

**Tasks**:
1. Create COT list page calling GET /api/cots
2. Implement COT card/list item component
3. Add navigation to detail view
4. Create detail page showing COT information
5. Add delete functionality with confirmation
6. Implement loading and error states

**Acceptance Criteria**:
- List page shows all COTs from backend
- Can click on COT to view details
- Can delete COT from detail view
- Loading spinners shown during API calls
- Error messages displayed on failures

### Phase 5: COT Editor
**Goal**: Create and edit COTs with DSL code

**Tasks**:
1. Create COT creation page with form
2. Add code editor component for DSL input
3. Implement DSL validation on client side
4. Add syntax highlighting for Kotlin DSL
5. Create edit page reusing editor component
6. Show validation errors inline
7. Test creating and editing COTs

**Acceptance Criteria**:
- Can create new COT with DSL code
- Code editor provides good UX (syntax highlighting, indentation)
- Validation errors shown before submission
- Can edit existing COT and save changes
- Changes persist in repository

### Phase 6: Generation Interface
**Goal**: Generate output with parameter inputs

**Tasks**:
1. Create generation page for specific COT
2. Build dynamic parameter form based on COT schema
3. Implement parameter input fields for different types
4. Add generate button and output display
5. Show generated output with copy functionality
6. Handle generation errors gracefully

**Acceptance Criteria**:
- Form dynamically adapts to COT schema
- Can input parameters of various types
- Generated output displayed correctly
- Can copy output to clipboard
- Generation errors shown with clear messages

### Phase 7: Testing & Documentation
**Goal**: Ensure quality and maintainability

**Tasks**:
1. Write unit tests for repository
2. Write integration tests for API endpoints
3. Add frontend component tests
4. Create end-to-end test for critical flows
5. Write comprehensive README
6. Document API with examples
7. Add inline code documentation

**Acceptance Criteria**:
- Test coverage > 70% for backend
- All critical user flows have E2E tests
- README explains how to run and use the app
- API documented with request/response examples

### Phase 8: Polish & Enhancement
**Goal**: Improve UX and production readiness

**Tasks**:
1. Add responsive design for mobile
2. Improve error messages and validation
3. Add loading states everywhere
4. Implement toast notifications
5. Add keyboard shortcuts for editor
6. Optimize bundle size
7. Add health check endpoint
8. Document deployment process

**Acceptance Criteria**:
- App works well on mobile devices
- All user actions provide clear feedback
- No console errors in browser
- Production build optimized
- Deployment instructions available

## Technical Considerations

### Error Handling Strategy

Following project guidelines, use Arrow's Raise DSL:

```kotlin
// Repository
interface CotRepository {
    fun create(cot: Cot, dslCode: String): Either<DomainError, StoredCot>
    fun findById(id: String): Either<DomainError, StoredCot>
    fun update(id: String, cot: Cot, dslCode: String): Either<DomainError, StoredCot>
    fun delete(id: String): Either<DomainError, Unit>
    fun list(): Either<DomainError, List<StoredCot>>
}

// Ktor route handling
get("/api/cots/{id}") {
    val id = call.parameters["id"] ?: return@get call.respond(
        HttpStatusCode.BadRequest,
        ErrorResponse("InvalidRequest", "Missing id parameter")
    )
    
    repository.findById(id)
        .fold(
            ifLeft = { error ->
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("CotNotFound", error.toString())
                )
            },
            ifRight = { cot ->
                call.respond(HttpStatusCode.OK, cot.toResponse())
            }
        )
}
```

### Concurrency & State Management

**Backend**:
- Use `ConcurrentHashMap` for in-memory storage
- Generate UUIDs for COT ids
- Use Instant for timestamps

**Frontend**:
- Use Kobweb's state management (Compose-style)
- Implement optimistic updates where appropriate
- Show loading indicators during async operations

### Serialization Challenges

The `Cot` domain object from `cot-dsl` may not be directly serializable. Options:

1. **Store DSL code as string**: Keep original DSL code and re-parse on retrieval
2. **Create serializable DTOs**: Map domain objects to serializable representations
3. **Custom serializers**: Implement kotlinx.serialization serializers for domain types

**Recommendation**: Store DSL code as string (option 1) for simplicity and to preserve user's original formatting.

### Frontend State Flow

```
User Action → API Call → Loading State → Success/Error State → UI Update
```

Example:
```kotlin
val cotsState = mutableStateOf<List<CotSummary>>(emptyList())
val loadingState = mutableStateOf(false)
val errorState = mutableStateOf<String?>(null)

fun loadCots() {
    loadingState.value = true
    errorState.value = null
    
    apiClient.getCots()
        .onSuccess { cots ->
            cotsState.value = cots
            loadingState.value = false
        }
        .onFailure { error ->
            errorState.value = error.message
            loadingState.value = false
        }
}
```

## Testing Strategy

### Backend Tests

**Unit Tests**:
- Repository CRUD operations
- DSL parsing and validation
- Error mapping functions
- Parameter conversion logic

**Integration Tests**:
- API endpoint responses
- Error handling flows
- Generation with various COT types

### Frontend Tests

**Component Tests**:
- COT list rendering
- Form validation
- Parameter input handling

**E2E Tests**:
- Create COT → View → Edit → Generate → Delete flow
- Error scenarios (invalid DSL, missing parameters)

### Test Data

Create sample COTs for testing:
```kotlin
// Simple static template
cot("Static") {
    "Hello, World!".text
}

// Template with parameters
cot("Greeting") {
    "Hello, ".text
    Params.name ifTrueThen "User"
}

// Complex template
cot("Complex") {
    conditional("showHeader") {
        section {
            static("=== Header ===\n")
        }
    }
    repeat("count") {
        section {
            static("Item ")
            dynamic("itemName")
            static("\n")
        }
    }
}
```

## Deployment

### Development Mode
```bash
# Terminal 1 - Backend
./gradlew :cot-simple-endpoints:run

# Terminal 2 - Frontend (Kobweb)
./gradlew :cot-simple-endpoints:kobwebStart
```

### Production Build
```bash
# Build backend JAR
./gradlew :cot-simple-endpoints:shadowJar

# Build frontend
./gradlew :cot-simple-endpoints:kobwebExport

# Run
java -jar cot-simple-endpoints/build/libs/cot-simple-endpoints-all.jar
```

## Future Enhancements

Post-MVP features to consider:

1. **Persistence**: Replace in-memory repository with database (PostgreSQL, MongoDB)
2. **Authentication**: Add user authentication and authorization
3. **Versioning**: Track COT versions and allow rollback
4. **Templates Library**: Pre-built templates users can clone
5. **Collaboration**: Real-time collaborative editing
6. **Export/Import**: Export COTs as files, import from files
7. **API Documentation**: Swagger/OpenAPI integration
8. **Metrics**: Usage analytics and performance monitoring
9. **Cloud Storage**: Store generated outputs in cloud storage
10. **Webhooks**: Trigger COT generation on external events

## References

- **Kobweb Documentation**: https://kobweb.varabyte.com/
- **Ktor Documentation**: https://ktor.io/docs/
- **Arrow-kt Documentation**: https://arrow-kt.io/
- **Project Coding Style**: `/.ai/CODING_STYLE.md`
- **Project Exception Handling**: `/.ai/EXCEPTION_HANDLING.md`
- **COT DSL Domain Plan**: `/cot-dsl/.ai/DSL_DOMAIN_PLAN.md`

## Conclusion

This plan provides a comprehensive roadmap for implementing the COT Simple Editor. By following the phased approach and adhering to the project's functional programming principles with Arrow-kt, we will create a maintainable, type-safe application that provides a great user experience for working with configurable templates.

The use of Kobweb for the frontend enables a modern, reactive UI while keeping the entire stack in Kotlin, and Ktor provides a lightweight, flexible backend framework. The in-memory repository keeps the MVP simple while allowing for easy extension to persistent storage in the future.
