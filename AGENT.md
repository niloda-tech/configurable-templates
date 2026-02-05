# AGENT.md - AI/LLM Development Guide

## Purpose
This document provides guidance for AI/LLM systems working on the Configurable Templates (COT) project. It explains the project structure, conventions, and available documentation resources.

## Project Overview

**Configurable Templates (COT)** is a Kotlin-based system for creating and managing configurable code templates using a Domain Specific Language (DSL). The project consists of three main modules:

1. **cot-dsl** - Core DSL library for defining templates
2. **cot-simple-endpoints** - Ktor-based REST API backend
3. **cot-frontend** - Kobweb-based web UI frontend

## Repository Structure

```
configurable-templates/
├── .ai/                        # AI/LLM guidance documents
│   ├── CODING_STYLE.md        # Code style conventions
│   ├── EXCEPTION_HANDLING.md  # Error handling patterns
│   ├── CONTRIBUTING.md        # Contribution guidelines
│   └── ...                    # Other development guides
├── cot-dsl/                   # Core DSL library
├── cot-simple-endpoints/      # Backend API
├── cot-frontend/              # Frontend UI
│   └── src/jsMain/kotlin/
│       └── com/niloda/
│           ├── LLM.md                # Root package documentation
│           ├── api/LLM.md            # API client package
│           ├── components/LLM.md     # UI components package
│           ├── pages/LLM.md          # Page routes package
│           └── pages/cots/LLM.md     # COT CRUD pages package
└── AGENT.md                   # This file

```

## Package-Level LLM Documentation

**IMPORTANT**: Each package in `cot-frontend` has dedicated `LLM.md` files that provide detailed guidance on:

- **Purpose**: What the package does
- **Public API**: Entry points and key functions
- **Data Contracts**: Type contracts and invariants
- **Patterns**: State management, navigation, error handling
- **Rules & Constraints**: Important limitations
- **Extension Points**: How to add new features
- **Dependencies**: What the package depends on

### Available LLM Documentation

1. **`cot-frontend/src/jsMain/kotlin/com/niloda/LLM.md`**
   - Application entry point and initialization
   - Global styles, CSS injection, lifecycle

2. **`cot-frontend/src/jsMain/kotlin/com/niloda/api/LLM.md`**
   - HTTP API client for backend communication
   - DTOs, Result<T> error handling, environment detection

3. **`cot-frontend/src/jsMain/kotlin/com/niloda/components/LLM.md`**
   - Reusable UI components (CotEditor, Toast, PageLayout, LoadingSpinner)
   - Validation rules, keyboard shortcuts, styling patterns

4. **`cot-frontend/src/jsMain/kotlin/com/niloda/pages/LLM.md`**
   - Top-level page routes and navigation
   - Page structure patterns, data loading

5. **`cot-frontend/src/jsMain/kotlin/com/niloda/pages/cots/LLM.md`**
   - COT-specific CRUD pages (Create, Edit, View, Generate)
   - State management, route parameters, navigation flows

**Always consult the relevant `LLM.md` file** when working within a package. These files are authoritative guides for that package's architecture and patterns.

## Key Development Guides

All detailed development guides are in the `.ai/` directory:

- **`.ai/CODING_STYLE.md`** - Functional programming with Arrow-kt, code conventions
- **`.ai/EXCEPTION_HANDLING.md`** - Error handling with Either<DomainError, T>
- **`.ai/CONTRIBUTING.md`** - How to contribute to the project
- **`.ai/COT_ROUTES_LLM_STYLE.md`** - Routing patterns and conventions
- **`.ai/HIGH_LEVEL_PLAN.md`** - Project roadmap and implementation plan

Additional implementation details:
- **`.ai/ARCHITECTURE.md`** - Module separation decisions
- **`.ai/COT_EDITOR_VALIDATION.md`** - Validation rules reference
- **`.ai/BUNDLE_OPTIMIZATION.md`** - Performance optimization strategies
- **`.ai/DEPLOYMENT.md`** - Production deployment guide
- **`.ai/PHASE5_IMPLEMENTATION.md`** - Editor implementation summary
- **`.ai/PHASE8_SUMMARY.md`** - Polish & enhancement summary

## Quick Start Commands

### Build
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :cot-dsl:build
./gradlew :cot-simple-endpoints:build
./gradlew :cot-frontend:build
```

### Test
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew :cot-simple-endpoints:test :cot-simple-endpoints:jacocoTestReport
```

### Run Backend
```bash
./gradlew :cot-simple-endpoints:run
# Starts on http://localhost:8080
```

### Run Frontend
```bash
cd cot-frontend
kobweb run
# Starts on http://localhost:8081
```

## Code Standards

### Backend (cot-simple-endpoints)
- **Functional Programming**: Use Arrow-kt's `Either<DomainError, T>`
- **No Exceptions**: Never throw exceptions in business logic
- **Immutable Data**: Prefer immutable data structures
- **Comprehensive Tests**: Maintain high test coverage

### Frontend (cot-frontend)
- **Compose/Kobweb**: Use Kobweb conventions (@Page, @Composable)
- **State Management**: Use `remember`, `LaunchedEffect`, `DisposableEffect`
- **Error Handling**: Use `Result<T>` for API calls
- **Validation**: Client-side validation in components, server-side in API

## Common Conventions

1. **Package-Based Organization**: Each package has focused responsibility
2. **Documentation Co-location**: `LLM.md` files live alongside code
3. **Type Safety**: Leverage Kotlin's type system
4. **Reactive UI**: Use Compose state management patterns
5. **Clean Architecture**: Separate concerns (API, components, pages)

## Making Changes

When modifying the codebase:

1. **Read the relevant `LLM.md`** for the package you're working in
2. **Follow patterns** established in that package
3. **Consult `.ai/` guides** for cross-cutting concerns
4. **Run tests** to validate changes
5. **Update documentation** if you change contracts or patterns

## Finding Information

- **"How do I work with package X?"** → Read `X/LLM.md`
- **"What's the code style?"** → See `.ai/CODING_STYLE.md`
- **"How do I handle errors?"** → See `.ai/EXCEPTION_HANDLING.md`
- **"How do I add a new page?"** → See `cot-frontend/.../pages/LLM.md`
- **"How do I add a new API endpoint?"** → See `cot-frontend/.../api/LLM.md`
- **"What are the validation rules?"** → See `.ai/COT_EDITOR_VALIDATION.md`

## Philosophy

This project follows these principles:

- **Documentation Lives with Code**: Package-level guides alongside implementation
- **AI-Friendly Patterns**: Consistent, discoverable conventions
- **Explicit Over Implicit**: Clear contracts and invariants
- **Type Safety**: Leverage the compiler to prevent errors
- **Separation of Concerns**: Clean boundaries between modules and layers

## Getting Help

- Check `README.md` for general project information
- Read module-specific READMEs: `cot-dsl/`, `cot-simple-endpoints/`, `cot-frontend/`
- Consult `LLM.md` files for package-specific guidance
- Review `.ai/` directory for detailed development guides

---

**Remember**: This repository uses package-level `LLM.md` files as the primary documentation for AI/LLM systems. Always start by reading the relevant `LLM.md` for the code you're working on.
