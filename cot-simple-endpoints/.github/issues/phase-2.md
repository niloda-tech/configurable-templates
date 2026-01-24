## Goal
Implement COT generation with parameter validation.

## Tasks
- [ ] Create `GenerateRequest` model with parameter map
- [ ] Implement parameter parsing from JSON to RenderParams
- [ ] Add `/api/cots/{id}/generate` endpoint
- [ ] Integrate with `generate()` function from cot-dsl
- [ ] Handle generation errors and map to error responses
- [ ] Test generation with various parameter combinations

## Acceptance Criteria
- Can generate output from stored COT with parameters
- Missing parameters return clear error messages
- Type mismatches are caught and reported
- Generated output matches expected format

## Dependencies
- Requires Phase 1 to be complete

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-2-generation-endpoint)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
