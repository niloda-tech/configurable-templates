### Plan to Implement the COTs DSL Domain (aligned with updated Coding Style; no code changes)

Below is a checkbox-style plan informed by the project’s top-level guidance: `README.md`, `CODING_STYLE.md`, `EXCEPTION_HANDLING.md`, `CONTRIBUTING.md`, and the existing `HIGH_LEVEL_PLAN.md`.

#### 0) Alignment & Foundations
- [x] Review coding style principles (immutability, Arrow usage, expression-oriented) from `CODING_STYLE.md`
- [x] Review typed error handling with the latest Arrow Raise DSL and `either {}` from `CODING_STYLE.md` and `EXCEPTION_HANDLING.md`
- [x] Review contributing expectations from `CONTRIBUTING.md`
- [x] Review current high-level goals in `HIGH_LEVEL_PLAN.md`
- [ ] Confirm scope and acceptance criteria for the initial DSL domain deliverable (MVP boundaries, out-of-scope items)

#### 1) Core Domain Confirmation (Immutable, Arrow-first)
- [ ] Finalize domain vocabulary (template, section, parameter, data type, generator, constraints, defaults)
- [ ] Ensure existing domain types align with immutability and minimal responsibilities
- [ ] Define error taxonomy (`DomainError`) covering: invalid names, duplicate keys, missing required values, type mismatches, constraint violations, generator errors, parsing errors
- [ ] Provide lightweight, composable validations as `context(Raise<DomainError>)` functions per `CODING_STYLE.md`
- [ ] Map external/exceptional boundaries to typed errors per `EXCEPTION_HANDLING.md` using `Either.catch { ... }.mapLeft { ... }`

#### 2) DSL Surface Design (Builder-style, expression-oriented)
- [ ] Specify the top-level entrypoint (e.g., `cot { ... }` or `template { ... }`)
- [ ] Specify nested builders for `section { ... }`, `parameter { ... }`, `dataType(...)`, constraints, and defaults
- [ ] Decide on naming rules and consistent, readable function names per `CODING_STYLE.md`
- [ ] Define return types for all builders as `Either<DomainError, A>`; internal building/validation occurs in `either { ... }` with `Raise<DomainError>`
- [ ] Identify convenience helpers for common patterns (e.g., `requiredString`, `optionalInt`, `enumOf(...)`)
- [ ] Define the minimal ergonomic API that avoids nulls in favor of `Option` where applicable; use `option { ... }` when constructing optional pieces

#### 3) Validation Strategy (Raise DSL + Validated when needed)
- [ ] Use `either { ... }` blocks with `raise`, `ensure`, and `ensureNotNull` to short-circuit on critical validation failures
- [ ] Use `ValidatedNel<DomainError, A>` and `zipOrAccumulate` for cases where we need to accumulate multiple errors (e.g., validating all parameters)
- [ ] Define reusable validations as `context(Raise<DomainError>)` functions: `fun ensureX(...)`
- [ ] Provide canonical validations: non-empty names, unique identifiers, type constraints, required presence, default value compatibility, constraint satisfaction
- [ ] Where appropriate, use `recover { ... }` to locally transform/handle raised errors inside a builder without leaking exceptions

#### 4) Parsing & Construction
- [ ] Decide if MVP requires text parsing or only Kotlin-embedded DSL; if parsing is included, define parser boundaries and error mapping
- [ ] Convert thrown exceptions at boundaries using `Either.catch { ... }.mapLeft { DomainError.X }` then `.bind()` within `either { ... }`
- [ ] Keep construction functions pure and side-effect free; prohibit throwing exceptions

#### 5) Generators (Optional MVP Scope)
- [ ] Define a small, typed generator interface that can fail with `Either<DomainError, Output>`; implementation code should operate inside `either { ... }`
- [ ] Provide basic sample generator contracts (no implementation now) and expected integration points with the DSL outputs
- [ ] Identify boundaries to isolate IO and exception-throwing code and convert to typed errors at the edge via `Either.catch`

#### 6) Testing Strategy (TDD-friendly but no code yet)
- [ ] Identify unit test suites: DSL construction, validation paths, error mapping, and generator contracts
- [ ] Define representative test cases for happy paths and failures (type mismatch, duplicates, missing requireds, invalid defaults)
- [ ] Include tests for: `ensure`/`ensureNotNull` behavior, `recover` usage, `ValidatedNel` accumulation via `zipOrAccumulate`
- [ ] Plan property-based tests for idempotency and round-trips if parsing is in scope
- [ ] Use JUnit 5 with Kotlin test libs; prefer pure functions for easy testing

#### 7) Documentation
- [ ] Add/extend README section describing the DSL’s goals and usage patterns
- [ ] Provide small examples that reflect `CODING_STYLE.md` and `EXCEPTION_HANDLING.md` best practices, using Raise DSL idioms (no imports in snippets)
- [ ] Document the error taxonomy and how callers should handle `Either`/`Option`
- [ ] Document public API conventions: public: `Either<DomainError, A>`; internal: `context(Raise<DomainError>)` helpers

#### 8) Performance & Ergonomics
- [ ] Keep builders allocation-light; prefer immutable accumulation
- [ ] Ensure name lookups favor sets/maps for uniqueness checks
- [ ] Consider Arrow Optics for safe data updates if needed
- [ ] Avoid side effects inside computation blocks; keep them expression-oriented per `CODING_STYLE.md`

#### 9) API Stability & Versioning
- [ ] Define what is experimental vs. stable for the initial release
- [ ] Add clear migration expectations if API changes post-MVP

#### 10) Build & CI (Verification Only)
- [ ] Ensure Gradle config supports Arrow dependency set already present
- [ ] Plan to run `./gradlew build` and `./gradlew test` continuously during development as per `HIGH_LEVEL_PLAN.md`

#### 11) Acceptance Checklist (MVP Completion Criteria)
- [ ] A coherent DSL entrypoint and nested builders that compile and model the domain immutably
- [ ] Validation implemented via `Raise`/`either`, using `ensure`/`ensureNotNull`, with accumulated errors via `ValidatedNel` where appropriate
- [ ] All failure-capable functions return `Either<DomainError, A>`; no thrown exceptions for expected flows
- [ ] Optional pieces modelled with `Option`/`option { ... }` rather than nulls
- [ ] Unit tests cover success/failure, accumulation, and boundary exception conversion
- [ ] Minimal documentation and examples are updated to the latest Raise DSL style
- [ ] Build is green with Java 17 toolchain

### Plan to Implement the Generate Domain (no code changes)

This plan defines how the Generate domain will take a `Cot`, configure it with passed parameters, and produce the output using Arrow’s Raise DSL and the project’s coding style.

#### 0) Scope & Acceptance
- [ ] Inputs: a built `Cot` and a set of parameters provided by the caller
- [ ] Behavior: evaluate `Configurable` items against parameters and render `Section`s accordingly
- [ ] Output: `Either<DomainError, String>` for MVP; consider chunked/structured output later
- [ ] Out of scope (MVP): IO, file emission, streaming, external templating engines, async/concurrency

#### 1) Domain Additions
- [ ] Add `RenderParams` value object: an immutable map `Map<String, Any?>` or typed wrappers (Boolean, Number, String, List<Any?>)
- [ ] Extend `DomainError` with generation-related errors:
  - [ ] `MissingParameter(name: String)`
  - [ ] `TypeMismatch(name: String, expected: String, actual: String)`
  - [ ] Reuse `InvalidChoiceKey(key: String)`; consider `UnknownChoice(key: String, where: String)` if needed
  - [ ] `RepeatError(reason: String)`
- [ ] Consider small type aliases for readability: `typealias ParamName = String`

#### 2) Public API Surface
- [ ] `fun generate(cot: Cot, params: RenderParams): Either<DomainError, String>`
- [ ] Internal helpers on `Raise<DomainError>`:
  - [ ] `context(Raise<DomainError>) fun renderSection(section: Section, params: RenderParams): String`
  - [ ] `context(Raise<DomainError>) fun renderConfigurable(cfg: Configurable, params: RenderParams): String?` (nullable/Option to indicate omission)
  - [ ] `context(Raise<DomainError>) fun paramBoolean(name: String): Boolean`
  - [ ] `context(Raise<DomainError>) fun paramString(name: String): String`
  - [ ] `context(Raise<DomainError>) fun paramInt(name: String): Int` (or Number)
  - [ ] `context(Raise<DomainError>) fun paramList(name: String): List<Any?>`

#### 3) Semantics per Configurable
- [ ] Conditional: include `section` when `paramBoolean(parameterName)` is true; otherwise omit
- [ ] Repetition: two supported modes in MVP
  - [ ] Count-based: when the parameter is an `Int` (>= 0), repeat the `section` n times
  - [ ] Iteration-based: when the parameter is a `List<*>`, iterate items; expose item via a conventional parameter name like `it` for dynamic references (defer if not needed for MVP)
- [ ] OneOf: read `paramString(parameterName)`; validate the key is present in `choices`; render the selected section

#### 4) Rendering `Section`
- [ ] For `Section.Part.Static(content)`: append `content`
- [ ] For `Section.Part.Dynamic(parameterName)`: look up parameter by name, convert to string via domain rule:
  - [ ] Strings use as-is
  - [ ] Numbers via `.toString()`
  - [ ] Booleans via `.toString()` (or error if not intended in content)
  - [ ] Lists/Objects: raise `TypeMismatch` in MVP

#### 5) Parameter Handling & Validation
- [ ] Retrieval helpers use `ensureNotNull`/`ensure` to raise `MissingParameter` or `TypeMismatch`
- [ ] Consider a lightweight `ParamValue` sealed type to avoid `Any?` branching; defer if MVP suffices with runtime checks
- [ ] For iteration mode, validate positive counts and non-negative integers

#### 6) Composition & Accumulation
- [ ] Primary flow in `either { ... }` context
- [ ] For whole-template rendering, short-circuit on critical failures
- [ ] Optionally accumulate multiple dynamic part errors within a section using `ValidatedNel` later; MVP can short-circuit

#### 7) Testing Strategy
- [ ] Unit tests for happy paths:
  - [ ] Conditional true/false behaviors
  - [ ] Repetition by count (0, 1, many)
  - [ ] OneOf selection rendering with dynamic content
  - [ ] Mixed schema order rendering
- [ ] Failure tests:
  - [ ] Missing parameter for conditional/repetition/oneOf/dynamic
  - [ ] Type mismatch for each retrieval helper
  - [ ] Unknown choice key in `OneOf`
- [ ] Edge cases:
  - [ ] Empty schema outputs empty string
  - [ ] Dynamic rendering of numbers/booleans

#### 8) Documentation
- [ ] Add README section “Generate Domain” with examples:
  - [ ] Building a Cot via DSL and generating with params map
  - [ ] Error handling with `Either` and simple pattern matching
- [ ] Note on parameter typing expectations and conversions

#### 9) Performance & Ergonomics
- [ ] Use `StringBuilder` for rendering accumulation
- [ ] Keep functions pure; avoid side effects
- [ ] Avoid repeated map lookups where possible

#### 10) Future Enhancements (Post-MVP)
- [ ] Structured output model (chunks with metadata) instead of plain string
- [ ] Scoped parameter contexts for iteration (`it`, index)
- [ ] Formatting controls for joining repetitions (separators, prefix/suffix)
- [ ] Support for nested/optional parameters via `Option` and Arrow Optics
- [ ] Streaming/sequence-based generation

#### 11) Acceptance Criteria (MVP)
- [ ] `generate(Cot, RenderParams)` compiles and returns `Either<DomainError, String>`
- [ ] Conditional, Repetition (count), and OneOf semantics implemented and covered by tests
- [ ] Dynamic parts resolve and render primitive values; errors reported via typed `DomainError`
- [ ] Documentation updated with minimal examples
