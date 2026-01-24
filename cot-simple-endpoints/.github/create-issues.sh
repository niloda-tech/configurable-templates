#!/bin/bash
set -euo pipefail

issues_dir="$(dirname "$0")/issues"

ensure_label() {
  name=$1
  color=$2
  description=$3
  gh label create "$name" --color "$color" --description "$description" --force
}

setup_labels() {
  label_defs=(
    "phase-1:#0E8A16:Phase 1 work"
    "phase-2:#0E8A16:Phase 2 work"
    "phase-3:#0E8A16:Phase 3 work"
    "phase-4:#0E8A16:Phase 4 work"
    "phase-5:#0E8A16:Phase 5 work"
    "phase-6:#0E8A16:Phase 6 work"
    "phase-7:#0E8A16:Phase 7 work"
    "phase-8:#0E8A16:Phase 8 work"
    "backend:#1D76DB:Backend work"
    "frontend:#1D76DB:Frontend work"
    "testing:#D4C5F9:Testing-related"
    "documentation:#D4C5F9:Documentation-related"
    "ux:#1D76DB:UX/Design"
    "production:#1D76DB:Production ready"
  )

  for entry in "${label_defs[@]}"; do
    IFS=":" read -r label color desc <<<"$entry"
    ensure_label "$label" "$color" "$desc"
  done
}

create_issue() {
  title=$1
  labels=$2
  body_file=$3
  gh issue create \
    --title "$title" \
    --label "$labels" \
    --body-file "$body_file"
}

setup_labels

create_issue "Phase 1: Backend Foundation - COT Editor" "enhancement,phase-1,backend" "$issues_dir/phase-1.md"
create_issue "Phase 2: Generation Endpoint - COT Editor" "enhancement,phase-2,backend" "$issues_dir/phase-2.md"
create_issue "Phase 3: Kobweb Frontend Setup - COT Editor" "enhancement,phase-3,frontend" "$issues_dir/phase-3.md"
create_issue "Phase 4: COT List & Detail Views - COT Editor" "enhancement,phase-4,frontend" "$issues_dir/phase-4.md"
create_issue "Phase 5: COT Editor - Create and Edit COTs" "enhancement,phase-5,frontend" "$issues_dir/phase-5.md"
create_issue "Phase 6: Generation Interface - COT Editor" "enhancement,phase-6,frontend" "$issues_dir/phase-6.md"
create_issue "Phase 7: Testing & Documentation - COT Editor" "enhancement,phase-7,testing,documentation" "$issues_dir/phase-7.md"
create_issue "Phase 8: Polish & Enhancement - COT Editor" "enhancement,phase-8,ux,production" "$issues_dir/phase-8.md"
