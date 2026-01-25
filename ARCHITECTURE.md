# Architecture Decision: Separate Frontend Module

## Problem

The initial approach of using a multiplatform Kotlin project with both JVM and JS targets in the same module (`cot-simple-endpoints`) created dependency conflicts between Kobweb and Ktor:

- Kobweb requires specific versions of Compose and kotlinx libraries
- Ktor has its own dependency requirements
- Mixing these in a single multiplatform module led to "dependency hell"
- The `kobweb.application` plugin had compatibility issues with multiplatform projects containing JVM targets

## Solution

**Create completely separate modules** for frontend and backend:

```
configurable-templates/
├── cot-simple-endpoints/    # Backend module (JVM only)
│   ├── build.gradle.kts     # Ktor dependencies only
│   └── src/main/kotlin/     # Backend code
│
└── cot-frontend/            # Frontend module (JS only)
    ├── build.gradle.kts     # Kobweb dependencies only
    └── src/jsMain/kotlin/   # Frontend code
```

## Benefits

### 1. Dependency Isolation
- **Backend**: Pure Ktor server with no Kobweb dependencies
- **Frontend**: Pure Kobweb application with no Ktor server dependencies
- Zero dependency conflicts

### 2. Build Performance
- Compile only the module that changed
- Faster iteration during development
- Parallel builds possible

### 3. Clear Boundaries
- Frontend and backend are completely independent
- Can be deployed separately
- Can be versioned independently

### 4. Easier Troubleshooting
- Issues are isolated to specific module
- Clearer build errors
- Simpler dependency management

### 5. Flexibility
- Can update frontend libraries without touching backend
- Can update backend libraries without touching frontend
- Can add more frontend modules (e.g., admin panel) without affecting backend

## Implementation Details

### Backend Module (cot-simple-endpoints)

**Build Configuration:**
```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    application
}

dependencies {
    // Only backend dependencies
    implementation("io.ktor:ktor-server-core-jvm:3.0.1")
    implementation("io.ktor:ktor-server-cors-jvm:3.0.1")
    implementation("io.arrow-kt:arrow-core:2.2.0")
    // ... more backend dependencies
}
```

**Port:** 8080  
**Role:** REST API server with CORS enabled

### Frontend Module (cot-frontend)

**Build Configuration:**
```kotlin
plugins {
    kotlin("multiplatform") version "2.2.20"
    kotlin("plugin.compose") version "2.2.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("com.varabyte.kobweb.library") version "0.23.3"
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                // Only frontend dependencies
                implementation("com.varabyte.kobweb:kobweb-core:0.23.3")
                implementation("io.ktor:ktor-client-js:3.0.1")
                // ... more frontend dependencies
            }
        }
    }
}
```

**Port:** 8081  
**Role:** Kobweb SPA communicating with backend API

## Communication

The frontend and backend communicate via REST API:

- **Development**: Frontend calls `http://localhost:8080/api/*`
- **Production**: Frontend calls same origin (deployed together)

The API client auto-detects the environment:

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
}
```

## Running

### Development Mode

**Terminal 1 - Backend:**
```bash
./gradlew :cot-simple-endpoints:run
```

**Terminal 2 - Frontend:**
```bash
cd cot-frontend
kobweb run
```

### Production Build

**Backend:**
```bash
./gradlew :cot-simple-endpoints:build
java -jar cot-simple-endpoints/build/libs/*.jar
```

**Frontend:**
```bash
cd cot-frontend
kobweb export
# Serve static files from .kobweb/site/
```

## Testing

Each module can be tested independently:

**Backend:**
```bash
./gradlew :cot-simple-endpoints:test
```

**Frontend:**
```bash
./gradlew :cot-frontend:compileKotlinJs
```

## Lessons Learned

1. **Don't mix incompatible frameworks in a single module** - Even with multiplatform, Kobweb and Ktor fight over dependencies
2. **Modular architecture has benefits beyond organization** - It solves real technical problems like dependency conflicts
3. **Separate deployment units = more flexibility** - Can scale frontend and backend independently

## References

- [Kobweb Documentation](https://kobweb.varabyte.com/)
- [Kotlin Multiplatform Guide](https://kotlinlang.org/docs/multiplatform.html)
- [cot-frontend/README.md](../cot-frontend/README.md) - Frontend module documentation
- [cot-simple-endpoints/API_DOCUMENTATION.md](../cot-simple-endpoints/API_DOCUMENTATION.md) - Backend API documentation
