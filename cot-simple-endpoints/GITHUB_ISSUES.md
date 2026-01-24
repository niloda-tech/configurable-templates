# GitHub Issues for COT Simple Editor Implementation

This document now points to the individual issue bodies and helper script used by the CLI.

---

## Issue references

| Phase | Title | Labels | Body file |
| --- | --- | --- | --- |
| 1 | `Phase 1: Backend Foundation - COT Editor` | `enhancement,phase-1,backend` | `.github/issues/phase-1.md` |
| 2 | `Phase 2: Generation Endpoint - COT Editor` | `enhancement,phase-2,backend` | `.github/issues/phase-2.md` |
| 3 | `Phase 3: Kobweb Frontend Setup - COT Editor` | `enhancement,phase-3,frontend` | `.github/issues/phase-3.md` |
| 4 | `Phase 4: COT List & Detail Views - COT Editor` | `enhancement,phase-4,frontend` | `.github/issues/phase-4.md` |
| 5 | `Phase 5: COT Editor - Create and Edit COTs` | `enhancement,phase-5,frontend` | `.github/issues/phase-5.md` |
| 6 | `Phase 6: Generation Interface - COT Editor` | `enhancement,phase-6,frontend` | `.github/issues/phase-6.md` |
| 7 | `Phase 7: Testing & Documentation - COT Editor` | `enhancement,phase-7,testing,documentation` | `.github/issues/phase-7.md` |
| 8 | `Phase 8: Polish & Enhancement - COT Editor` | `enhancement,phase-8,ux,production` | `.github/issues/phase-8.md` |

---

## Bulk creation script

Use the executable script at `.github/create-issues.sh` to create every issue via `gh issue create`. Ensure the CLI is set up, then run `./.github/create-issues.sh` from the repository root. The script sources the `.github/issues/phase-*.md` files above.
