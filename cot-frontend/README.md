# COT Frontend

Kobweb-based frontend for the Configurable Templates Editor.

## Architecture

This is a **separate module** from the backend to avoid dependency conflicts. The frontend and backend have completely isolated dependency trees.

### Module Structure
```
cot-frontend/               # Frontend module (Kobweb, JS only)
├── build.gradle.kts        # Frontend dependencies (Kobweb, Compose HTML, Ktor Client)
├── .kobweb/
│   └── conf.yaml           # Kobweb configuration
└── src/jsMain/kotlin/
    └── com/niloda/
        ├── AppEntry.kt     # Main app entry point with @App annotation
        ├── api/            # API client for backend communication
        ├── pages/          # Kobweb pages (@Page annotations)
        └── components/     # Reusable UI components

cot-simple-endpoints/       # Backend module (Ktor, JVM only)
└── src/main/kotlin/        # Backend code (completely separate)
```

## Why Separate Modules?

Mixing Kobweb and Ktor in a single multiplatform module creates:
- Dependency version conflicts (Compose vs Ktor)
- Build complexity and "dependency hell"
- Tight coupling between frontend and backend

Separate modules provide:
- ✅ Clean dependency isolation
- ✅ Independent versioning
- ✅ Faster builds (compile only what changed)
- ✅ Clear architecture boundaries

## Technology Stack

- **Kotlin**: 2.2.20 (JS target only)
- **Kobweb**: 0.23.3 (application plugin with `configAsKobwebApplication`)
- **Compose HTML**: 1.8.0
- **Ktor Client**: 3.0.1 (for API calls)
- **kotlinx.serialization**: 1.7.3

## Features

### Pages
- **Home** (`/`) - Welcome page with feature overview
- **Templates** (`/templates`) - List all COTs from backend
- **About** (`/about`) - Information about the application

### API Integration
- Auto-detects backend URL:
  - Development: `http://localhost:8080`
  - Production: Same origin as frontend
- Full REST client with error handling
- Type-safe models matching backend API

## Running

### Prerequisites
```bash
# Install Kobweb CLI (one-time setup)
brew install varabyte/tap/kobweb  # macOS
# or follow https://kobweb.varabyte.com/docs/getting-started
```

### Development

**Start Backend** (in root directory):
```bash
./gradlew :cot-simple-endpoints:run
```
Backend runs on http://localhost:8080

**Start Frontend**:
```bash
cd cot-frontend
kobweb run
```
Frontend runs on http://localhost:8081

### Building

**Compile JS**:
```bash
./gradlew :cot-frontend:compileKotlinJs
```

**Export for Production**:
```bash
cd cot-frontend
kobweb export
```

## Project Structure

```kotlin
// AppEntry.kt - Main app entry point
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            content()
        }
    }
}

// Pages follow Kobweb convention
package com.niloda.pages

@Page  // Routes to /
@Composable
fun HomePage() { ... }

@Page  // Routes to /templates
@Composable
fun TemplatesPage() { ... }
```

## Key Configuration

### build.gradle.kts
Uses `kobweb.application` plugin (not `library`) and `configAsKobwebApplication`:

```kotlin
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    kotlin("multiplatform") version "2.2.20"
    id("com.varabyte.kobweb.application") version "0.23.3"
}

kotlin {
    configAsKobwebApplication("cot-frontend")
}
```

### .kobweb/conf.yaml
Specifies server configuration and file paths:

```yaml
site:
  title: "COT Editor"
  
server:
  files:
    dev:
      contentRoot: "build/processedResources/js/main/public"
      script: "build/kotlin-webpack/js/developmentExecutable/cot-frontend.js"
  port: 8081
```

## API Client

The API client automatically detects the environment:

```kotlin
object ApiClient {
    private val apiBaseUrl = run {
        val origin = window.location.origin
        if (origin.contains(":8081")) {
            "http://localhost:8080"  // Dev mode
        } else {
            origin  // Production
        }
    }
    
    suspend fun listCots(): Result<CotListResponse>
    suspend fun getCot(id: String): Result<CotDetailResponse>
    suspend fun createCot(request: CreateCotRequest): Result<CotDetailResponse>
    // ... more methods
}
```

## Development Workflow

1. Make frontend changes in `cot-frontend/src/jsMain/kotlin/`
2. Kobweb hot-reloads automatically
3. Changes are isolated from backend
4. No dependency conflicts!

## Changes from Previous Version

This version uses the **proper Kobweb application structure**:
- ✅ Uses `kobweb.application` plugin (not `library`)
- ✅ Uses `configAsKobwebApplication` helper
- ✅ Has proper `@App` annotation in AppEntry.kt
- ✅ Updated .kobweb/conf.yaml with correct file paths
- ✅ Uses Compose 1.8.0 (matching Kobweb requirements)

## Next Steps

- [ ] Add Create COT page with form
- [ ] Add Edit COT page
- [ ] Add Generate Output page with parameter form
- [ ] Improve styling and UX
- [ ] Add loading states and animations

## Troubleshooting

**Q: Build errors with Kobweb plugin?**  
A: Make sure you're using `kobweb.application` plugin and `configAsKobwebApplication`. The `kobweb.library` plugin doesn't work for standalone applications.

**Q: Can't reach backend?**  
A: Ensure backend is running on port 8080 and CORS is enabled.

**Q: Dependency conflicts?**  
A: This module is completely separate from backend - no shared dependencies!

## References

- [Kobweb Documentation](https://kobweb.varabyte.com/)
- [Backend API Documentation](../cot-simple-endpoints/API_DOCUMENTATION.md)
- [Implementation Plan](../cot-simple-endpoints/IMPLEMENTATION_PLAN.md)
