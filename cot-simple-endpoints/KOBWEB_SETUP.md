# Kobweb Frontend Setup

This document describes the Kobweb frontend setup for the cot-simple-endpoints module.

## Structure

The cot-simple-endpoints module is now a Kotlin Multiplatform project with:

### Source Sets

- **jvmMain**: Ktor backend (port 8080)
  - Location: `src/jvmMain/kotlin`
  - Contains the existing REST API

- **jsMain**: Kobweb frontend (port 8081)
  - Location: `src/jsMain/kotlin/com/niloda/cot/frontend`
  - Pages: `/pages` directory
  - Components: `/components` directory
  - API Client: `/api` directory

### Configuration

- **Kobweb Config**: `.kobweb/conf.yaml`
- **Build Config**: `build.gradle.kts` (multiplatform setup)

## Building

### Backend Only
```bash
./gradlew :cot-simple-endpoints:jvmJar
```

### Frontend Only
```bash
./gradlew :cot-simple-endpoints:jsBrowserProductionWebpack
```

### Both
```bash
./gradlew :cot-simple-endpoints:build
```

## Running

### Backend Server
```bash
./gradlew :cot-simple-endpoints:run
```
Server will start on http://localhost:8080

### Kobweb Dev Server
```bash
cd cot-simple-endpoints
kobweb run
```
Frontend will start on http://localhost:8081

Note: You need to install Kobweb CLI first:
```bash
# Using Homebrew (Mac)
brew install varabyte/tap/kobweb

# Or download from https://github.com/varabyte/kobweb
```

## Pages

- **/** - Home page with introduction
- **/templates** - List of templates (fetches from backend API)
- **/about** - About page

## API Communication

The frontend uses Ktor client to communicate with the backend:
- Base URL: `http://localhost:8080/api/cot`
- See `src/jsMain/kotlin/com/niloda/cot/frontend/api/ApiClient.kt`

## Technology Stack

- **Kotlin**: 2.2.20
- **Kobweb**: 0.23.3
- **Compose Multiplatform**: 1.7.3  
- **Ktor Client**: 3.0.1
- **Kotlinx Serialization**: 1.7.3

## Notes

- The frontend uses Silk (Kobweb's component library) for styling
- Navigation is handled automatically by Kobweb based on `@Page` annotations
- API models are duplicated in jsMain (future: move to commonMain for sharing)
