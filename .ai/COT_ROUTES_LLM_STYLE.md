# LLM Style Guide for `cot-simple-endpoints`

Use these directives when generating or modifying code in `cot-simple-endpoints`, mirroring the established patterns in `cot-simple-endpoints/src/main/kotlin/com/niloda/cot/simple/api/CotRoutes.kt`.

## Routing and Structure
- Group COT HTTP handlers under `Route.cotRoutes(repository)` with nested `route("/api/cots")`.
- Prefer small private helpers colocated in the same file for responses and model mapping.

## Request Handling
- Parse requests with `call.receive<Dto>()` using explicit request DTOs (e.g., `CreateCotRequest`, `UpdateCotRequest`).
- Extract route params via `fromNullable(call.parameters["id"])` and handle absence with a `BadRequest` response.

## Response Pattern
- Provide response helpers on `RoutingContext`: `ok`, `created`, `badRequest`, `notFound`, `internalServerError`, `noContent`.
- Reply with `ErrorResponse(errorCode, message)` for failures; use stable error codes like `InvalidRequest`, `InvalidDsl`, `CotNotFound`, `CreateError`, `InternalError`.
- Return status codes explicitly via `HttpStatusCode` companions (`BadRequest`, `NotFound`, `OK`, etc.).

## Functional Flow (Arrow)
- Operate with `fold` on `Either`/`Option` to branch success/error without exceptions.
- Keep handlers expression-oriented; avoid imperative early returns.

## DSL Usage
- Build COTs through the DSL: `cot(request.name) { ... }`.
- When DSL parsing fails, surface `InvalidDsl` as a `BadRequest`.

## Data Mapping
- Map persistence models via private extensions: `StoredCot.toSummary()` and `StoredCot.toDetailResponse()`; keep definitions near the routes file.
- Use `createdAt`/`updatedAt` string formatting consistent with existing conversions (`toString()`).

## Formatting
- Kotlin, 4-space indent, no trailing commas. Blank lines separate handler blocks and helper sections.
- Use explicit imports for status codes rather than wildcarding; keep import list tight.

Follow these rules unless there is a stronger project-wide directive in `.ai/CODING_STYLE.md` or other `.ai` policies.
