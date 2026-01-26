# COT Editor - Validation Rules

## Overview
The COT Editor implements comprehensive client-side validation to provide immediate feedback to users and prevent invalid submissions to the backend API.

## Validation Rules

### Template Name Validation

#### Rule 1: Required Field
- **Error Message**: "Template name is required"
- **Trigger**: When the name field is blank
- **Why**: Every COT must have a unique identifier

#### Rule 2: Minimum Length
- **Error Message**: "Template name must be at least 3 characters"
- **Trigger**: When name is 1-2 characters long
- **Why**: Ensures meaningful template names

#### Rule 3: Valid Identifier Pattern
- **Error Message**: "Template name must start with a letter and contain only letters, numbers, and underscores"
- **Pattern**: `^[A-Za-z][A-Za-z0-9_]*$`
- **Trigger**: When name contains invalid characters or doesn't start with a letter
- **Why**: Ensures names are valid Kotlin identifiers and can be safely used in code

**Valid Examples:**
- `MyTemplate`
- `GreetingTemplate`
- `User_Profile_Template`
- `Template123`

**Invalid Examples:**
- `123Template` (starts with number)
- `My-Template` (contains hyphen)
- `my.template` (contains period)
- `my template` (contains space)

### DSL Code Validation

#### Rule 1: Required Field
- **Error Message**: "DSL code is required"
- **Trigger**: When the DSL code field is blank
- **Why**: A COT must have template content

#### Rule 2: Must Start with cot(
- **Error Message**: "DSL code must start with 'cot('"
- **Trigger**: When trimmed code doesn't start with `cot(`
- **Why**: Ensures code follows the required DSL syntax

#### Rule 3: Balanced Braces
- **Error Message**: "Unbalanced braces: X opening '{' but Y closing '}'"
- **Trigger**: When number of `{` doesn't match number of `}`
- **Why**: Prevents basic syntax errors in the DSL

#### Rule 4: Balanced Parentheses
- **Error Message**: "Unbalanced parentheses: X opening '(' but Y closing ')'"
- **Trigger**: When number of `(` doesn't match number of `)`
- **Why**: Prevents basic syntax errors in the DSL

## Implementation Details

### Real-Time Validation
Validation runs automatically whenever the user types in either field:
```kotlin
LaunchedEffect(name, dslCode) {
    validationErrors = validateCotInput(name, dslCode)
}
```

### Performance Optimization
- Regex pattern compiled once at module level
- Single-pass character counting for brackets
- Minimal string operations

### UI Integration
- Submit button automatically disabled when validation errors exist
- Errors displayed in a prominent red-bordered box
- Error messages are clear and actionable
- Updates happen in real-time as user types

## Example Validation States

### Valid State
```
Name: GreetingTemplate
Code: cot("GreetingTemplate") { "Hello".text }
Errors: []
Submit Button: Enabled
```

### Invalid State - Multiple Errors
```
Name: a
Code: cot("Test") { "Hello".text
Errors: [
  "Template name must be at least 3 characters",
  "Unbalanced braces: 1 opening '{' but 0 closing '}'"
]
Submit Button: Disabled
```

### Invalid State - Name Pattern
```
Name: my-template
Code: cot("MyTemplate") { "Hello".text }
Errors: [
  "Template name must start with a letter and contain only letters, numbers, and underscores"
]
Submit Button: Disabled
```

## Server-Side Validation
The client-side validation is a first line of defense. The backend API also performs validation:
- DSL parsing and syntax checking
- Name uniqueness checking
- Additional semantic validation

This dual-layer approach provides:
1. **Immediate feedback** (client-side)
2. **Authoritative validation** (server-side)

## Testing Validation

To test validation rules:

1. **Empty name**: Clear the name field → See "Template name is required"
2. **Short name**: Enter "ab" → See "Template name must be at least 3 characters"
3. **Invalid name**: Enter "123Test" → See pattern error
4. **Empty code**: Clear DSL code → See "DSL code is required"
5. **Missing cot(**: Enter "test {}" → See "DSL code must start with 'cot('"
6. **Unbalanced braces**: Enter "cot("Test") { test" → See brace error
7. **Unbalanced parens**: Enter "cot("Test" { }" → See parenthesis error
8. **Valid input**: Enter valid name and code → No errors, button enabled
