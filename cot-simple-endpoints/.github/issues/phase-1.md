## Goal
Set up Ktor server with basic CRUD endpoints for COT management.

## Tasks
- [ ] Update `build.gradle.kts` to add Arrow dependencies
- [ ] Add dependency on `:cot-dsl` module
- [ ] Implement `InMemoryCotRepository` with concurrent-safe storage
- [ ] Create API request/response models with kotlinx.serialization
- [ ] Implement Ktor routes for CRUD operations
- [ ] Add error handling using Arrow's Either
- [ ] Test endpoints with curl/HTTP client

## Acceptance Criteria
- All CRUD endpoints return proper HTTP status codes
- Repository handles concurrent access safely
- Errors are returned as typed DomainError converted to ErrorResponse
- Can create, read, update, delete COTs via API

## Dependencies
None - This is the foundation phase

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-1-backend-foundation)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
