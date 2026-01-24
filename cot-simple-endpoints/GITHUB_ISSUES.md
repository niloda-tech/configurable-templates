# GitHub Issues for COT Simple Editor Implementation

This document contains GitHub issue templates for each implementation phase. Copy and paste these into GitHub to create the issues.

---

## Issue 1: Phase 1 - Backend Foundation

**Title**: `Phase 1: Backend Foundation - COT Editor`

**Labels**: `enhancement`, `phase-1`, `backend`

**Body**:
```markdown
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
```

---

## Issue 2: Phase 2 - Generation Endpoint

**Title**: `Phase 2: Generation Endpoint - COT Editor`

**Labels**: `enhancement`, `phase-2`, `backend`

**Body**:
```markdown
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
```

---

## Issue 3: Phase 3 - Kobweb Frontend Setup

**Title**: `Phase 3: Kobweb Frontend Setup - COT Editor`

**Labels**: `enhancement`, `phase-3`, `frontend`

**Body**:
```markdown
## Goal
Initialize Kobweb project and basic layout.

## Tasks
- [ ] Add Kobweb Gradle plugin to build.gradle.kts
- [ ] Configure multiplatform build (jvm + js targets)
- [ ] Create basic Kobweb site structure
- [ ] Implement base layout with navigation
- [ ] Setup routing for main pages
- [ ] Configure API client for backend communication
- [ ] Test basic page rendering

## Acceptance Criteria
- Kobweb dev server starts successfully
- Can navigate between pages
- API client can communicate with Ktor backend
- Basic styling is applied

## Dependencies
- Phase 1 should be complete for API integration testing

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-3-kobweb-frontend-setup)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)
- Kobweb docs: https://kobweb.varabyte.com/

Part of the COT Simple Editor implementation plan.
```

---

## Issue 4: Phase 4 - COT List & Detail Views

**Title**: `Phase 4: COT List & Detail Views - COT Editor`

**Labels**: `enhancement`, `phase-4`, `frontend`

**Body**:
```markdown
## Goal
Display and navigate COTs in the UI.

## Tasks
- [ ] Create COT list page calling GET /api/cots
- [ ] Implement COT card/list item component
- [ ] Add navigation to detail view
- [ ] Create detail page showing COT information
- [ ] Add delete functionality with confirmation
- [ ] Implement loading and error states

## Acceptance Criteria
- List page shows all COTs from backend
- Can click on COT to view details
- Can delete COT from detail view
- Loading spinners shown during API calls
- Error messages displayed on failures

## Dependencies
- Requires Phase 3 (Frontend setup)
- Requires Phase 1 (Backend API)

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-4-cot-list--detail-views)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
```

---

## Issue 5: Phase 5 - COT Editor

**Title**: `Phase 5: COT Editor - Create and Edit COTs`

**Labels**: `enhancement`, `phase-5`, `frontend`

**Body**:
```markdown
## Goal
Create and edit COTs with DSL code in the UI.

## Tasks
- [ ] Create COT creation page with form
- [ ] Add code editor component for DSL input
- [ ] Implement DSL validation on client side
- [ ] Add syntax highlighting for Kotlin DSL
- [ ] Create edit page reusing editor component
- [ ] Show validation errors inline
- [ ] Test creating and editing COTs

## Acceptance Criteria
- Can create new COT with DSL code
- Code editor provides good UX (syntax highlighting, indentation)
- Validation errors shown before submission
- Can edit existing COT and save changes
- Changes persist in repository

## Dependencies
- Requires Phase 3 (Frontend setup)
- Requires Phase 1 (Backend API)

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-5-cot-editor)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
```

---

## Issue 6: Phase 6 - Generation Interface

**Title**: `Phase 6: Generation Interface - COT Editor`

**Labels**: `enhancement`, `phase-6`, `frontend`

**Body**:
```markdown
## Goal
Generate output with parameter inputs in the UI.

## Tasks
- [ ] Create generation page for specific COT
- [ ] Build dynamic parameter form based on COT schema
- [ ] Implement parameter input fields for different types
- [ ] Add generate button and output display
- [ ] Show generated output with copy functionality
- [ ] Handle generation errors gracefully

## Acceptance Criteria
- Form dynamically adapts to COT schema
- Can input parameters of various types
- Generated output displayed correctly
- Can copy output to clipboard
- Generation errors shown with clear messages

## Dependencies
- Requires Phase 3 (Frontend setup)
- Requires Phase 2 (Generation endpoint)

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-6-generation-interface)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
```

---

## Issue 7: Phase 7 - Testing & Documentation

**Title**: `Phase 7: Testing & Documentation - COT Editor`

**Labels**: `enhancement`, `phase-7`, `testing`, `documentation`

**Body**:
```markdown
## Goal
Ensure quality and maintainability through comprehensive testing and documentation.

## Tasks
- [ ] Write unit tests for repository
- [ ] Write integration tests for API endpoints
- [ ] Add frontend component tests
- [ ] Create end-to-end test for critical flows
- [ ] Write comprehensive README
- [ ] Document API with examples
- [ ] Add inline code documentation

## Acceptance Criteria
- Test coverage > 70% for backend
- All critical user flows have E2E tests
- README explains how to run and use the app
- API documented with request/response examples

## Dependencies
- Should be done alongside or after core features (Phases 1-6)

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-7-testing--documentation)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
```

---

## Issue 8: Phase 8 - Polish & Enhancement

**Title**: `Phase 8: Polish & Enhancement - COT Editor`

**Labels**: `enhancement`, `phase-8`, `ux`, `production`

**Body**:
```markdown
## Goal
Improve UX and production readiness.

## Tasks
- [ ] Add responsive design for mobile
- [ ] Improve error messages and validation
- [ ] Add loading states everywhere
- [ ] Implement toast notifications
- [ ] Add keyboard shortcuts for editor
- [ ] Optimize bundle size
- [ ] Add health check endpoint
- [ ] Document deployment process

## Acceptance Criteria
- App works well on mobile devices
- All user actions provide clear feedback
- No console errors in browser
- Production build optimized
- Deployment instructions available

## Dependencies
- Should be done after MVP features are complete (Phases 1-6)

## Related Documentation
- See [IMPLEMENTATION_PLAN.md](cot-simple-endpoints/IMPLEMENTATION_PLAN.md#phase-8-polish--enhancement)
- Track progress in [PLAN_TRACKING.md](cot-simple-endpoints/PLAN_TRACKING.md)

Part of the COT Simple Editor implementation plan.
```

---

## Creating Issues via GitHub CLI

If you have the GitHub CLI (`gh`) installed and authenticated, you can create all issues at once using this script:

```bash
#!/bin/bash
# Save this as create-issues.sh and run: chmod +x create-issues.sh && ./create-issues.sh

# Phase 1
gh issue create \
  --title "Phase 1: Backend Foundation - COT Editor" \
  --label "enhancement,phase-1,backend" \
  --body-file .github/issues/phase-1.md

# Phase 2
gh issue create \
  --title "Phase 2: Generation Endpoint - COT Editor" \
  --label "enhancement,phase-2,backend" \
  --body-file .github/issues/phase-2.md

# Phase 3
gh issue create \
  --title "Phase 3: Kobweb Frontend Setup - COT Editor" \
  --label "enhancement,phase-3,frontend" \
  --body-file .github/issues/phase-3.md

# Phase 4
gh issue create \
  --title "Phase 4: COT List & Detail Views - COT Editor" \
  --label "enhancement,phase-4,frontend" \
  --body-file .github/issues/phase-4.md

# Phase 5
gh issue create \
  --title "Phase 5: COT Editor - Create and Edit COTs" \
  --label "enhancement,phase-5,frontend" \
  --body-file .github/issues/phase-5.md

# Phase 6
gh issue create \
  --title "Phase 6: Generation Interface - COT Editor" \
  --label "enhancement,phase-6,frontend" \
  --body-file .github/issues/phase-6.md

# Phase 7
gh issue create \
  --title "Phase 7: Testing & Documentation - COT Editor" \
  --label "enhancement,phase-7,testing,documentation" \
  --body-file .github/issues/phase-7.md

# Phase 8
gh issue create \
  --title "Phase 8: Polish & Enhancement - COT Editor" \
  --label "enhancement,phase-8,ux,production" \
  --body-file .github/issues/phase-8.md
```

## Creating Issues via GitHub Web UI

1. Go to https://github.com/niloda-tech/configurable-templates/issues/new
2. Copy the title and body from each issue template above
3. Add the suggested labels
4. Click "Submit new issue"
5. Update PLAN_TRACKING.md with the issue numbers

## Suggested Labels to Create

If these labels don't exist in your repository, create them first:

- `phase-1`, `phase-2`, `phase-3`, `phase-4`, `phase-5`, `phase-6`, `phase-7`, `phase-8`
- `backend`, `frontend`, `testing`, `documentation`, `ux`, `production`

Colors:
- Phase labels: `#0E8A16` (green)
- Component labels: `#1D76DB` (blue)
- Type labels: `#D4C5F9` (purple)
