# Package: com.niloda.api

## Purpose
HTTP API client for communicating with the COT backend REST API. Provides type-safe client methods for all CRUD operations on Configurable Output Templates (COTs) and template generation functionality.

## Key Files
- **ApiClient.kt** - Main API client singleton with all HTTP operations

## Public API / Entry Points

### ApiClient Object
Singleton HTTP client that provides all backend API operations.

**Key Methods:**
- `suspend fun listCots(): Result<CotListResponse>` - Retrieve all COTs
- `suspend fun getCot(id: String): Result<CotDetailResponse>` - Get single COT by ID
- `suspend fun createCot(request: CreateCotRequest): Result<CotDetailResponse>` - Create new COT
- `suspend fun updateCot(id: String, request: UpdateCotRequest): Result<CotDetailResponse>` - Update existing COT
- `suspend fun deleteCot(id: String): Result<Unit>` - Delete a COT
- `suspend fun generateOutput(id: String, request: GenerateRequest): Result<GenerateResponse>` - Generate output from COT with parameters

### Data Transfer Objects (DTOs)

All DTOs are marked with `@Serializable` for kotlinx.serialization:

**Request Models:**
- `CreateCotRequest(name: String, dslCode: String)` - Request to create a new COT
- `UpdateCotRequest(name: String, dslCode: String)` - Request to update an existing COT
- `GenerateRequest(parameters: Map<String, JsonElement>)` - Request to generate output with dynamic parameters

**Response Models:**
- `CotListResponse(cots: List<CotSummary>)` - List of all COTs
- `CotSummary(id: String, name: String, createdAt: String, updatedAt: String)` - Summary info for COT list view
- `CotDetailResponse(id: String, name: String, dslCode: String, createdAt: String, updatedAt: String)` - Full COT details
- `GenerateResponse(output: String)` - Generated template output

## Data Contracts / Invariants

1. **All DTOs must be `@Serializable`** - Required for JSON serialization/deserialization
2. **HTTP client configuration** - Uses Ktor client with ContentNegotiation plugin
3. **JSON configuration**:
   - `ignoreUnknownKeys = true` - Tolerates extra fields from backend (forward compatibility)
   - `isLenient = true` - Relaxed parsing for development
   - `prettyPrint = true` - Human-readable JSON in logs

## Environment Behavior

### Base URL Auto-Detection
The API client automatically determines the backend URL based on the current environment:

- **Development**: If frontend runs on port 8081 → backend at `http://localhost:8080`
- **Production**: Uses same origin as frontend (single-domain deployment)

```kotlin
private val apiBaseUrl = run {
    val origin = window.location.origin
    if (origin.contains(":8081")) {
        "http://localhost:8080"  // Dev
    } else {
        origin  // Production
    }
}
```

## Error Handling

All API methods return `Result<T>`:
- **Success**: `Result.success(T)` contains the response data
- **Failure**: `Result.failure(Exception)` contains the error

**Usage Pattern:**
```kotlin
ApiClient.getCot(id)
    .onSuccess { response -> 
        // Handle success
    }
    .onFailure { error -> 
        // Handle error (network, HTTP, parsing)
    }
```

**No exceptions are thrown** - all errors are captured in `Result` wrapper.

## Rules / Constraints

1. **No UI Logic** - This package only handles HTTP communication and data serialization
2. **No Business Logic** - Validation and business rules belong in components/pages
3. **Immutable DTOs** - All data classes are immutable (`val` properties only)
4. **Suspend Functions** - All API methods are suspending (must be called from coroutine scope)
5. **Single Client Instance** - ApiClient is a singleton object (shared HTTP client)

## Extending / Adding New Endpoints

To add a new API endpoint:

1. **Define DTOs** (if needed):
```kotlin
@Serializable
data class NewRequest(val field: String)

@Serializable
data class NewResponse(val result: String)
```

2. **Add method to ApiClient**:
```kotlin
suspend fun newOperation(request: NewRequest): Result<NewResponse> = try {
    val response = client.post("$baseUrl/new-endpoint") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }
    Result.success(response.body())
} catch (e: Exception) {
    Result.failure(e)
}
```

3. **Follow patterns**:
   - Use `Result<T>` return type
   - Catch all exceptions
   - Use appropriate HTTP method (GET, POST, PUT, DELETE)
   - Set `ContentType.Application.Json` for request bodies
   - Use `.body()` to deserialize response

## Dependencies
- **Ktor Client** - HTTP client library
- **kotlinx.serialization** - JSON serialization
- **Ktor ContentNegotiation** - Automatic JSON handling
- **Browser API** - `window.location` for environment detection
