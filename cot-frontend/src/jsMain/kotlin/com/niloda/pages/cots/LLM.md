# Package: com.niloda.pages.cots

## Purpose
COT-specific CRUD (Create, Read, Update, Delete) pages for managing individual Configurable Output Templates. Provides dedicated pages for creating, viewing, editing, and generating output from templates.

## Key Files
- **Create.kt** - Create new COT template page
- **[id].kt** - View COT details and manage template (delete, edit actions)
- **Edit.kt** - Edit existing COT template
- **Generate.kt** - Generate output from template with parameter input

## Public API / Entry Points

### CreateCotPage
Form for creating a new COT template.

```kotlin
@Page("/cots/create")
@Composable
fun CreateCotPage()
```

- **Route**: `/cots/create`
- **State**:
  - `isSubmitting: Boolean` - Submission in progress
  - `error: String?` - Error message if creation fails
- **Components**: Uses `CotEditor` with empty initial values
- **Behavior**:
  - Pre-populates DSL editor with example template
  - Submits to `ApiClient.createCot()`
  - On success: Shows toast + navigates to `/cots/{newId}`
  - On failure: Shows error panel + toast
  - Cancel navigates to `/templates`

### CotDetailPage
View COT details with action buttons.

```kotlin
@Page("{id}")
@Composable
fun CotDetailPage()
```

- **Route**: `/cots/{id}` (dynamic ID parameter)
- **State**:
  - `cot: CotDetailResponse?` - Loaded COT data
  - `loading: Boolean` - Initial load state
  - `error: String?` - Load/delete error
  - `showDeleteConfirm: Boolean` - Delete confirmation modal
  - `deleting: Boolean` - Delete in progress
- **Behavior**:
  - Fetches COT on mount via `LaunchedEffect(id)`
  - Displays: name, DSL code (read-only), timestamps
  - Action buttons:
    - **Edit** → navigates to `/cots/edit/{id}`
    - **Generate** → navigates to `/cots/generate/{id}`
    - **Delete** → shows confirmation, calls `ApiClient.deleteCot()`, navigates to `/templates`
  - Breadcrumb back to `/templates`

### EditCotPage
Form for editing existing COT template.

```kotlin
@Page("/cots/edit/{id}")
@Composable
fun EditCotPage()
```

- **Route**: `/cots/edit/{id}` (dynamic ID parameter)
- **State**:
  - `cot: CotDetailResponse?` - Original COT data
  - `loading: Boolean` - Initial load state
  - `loadError: String?` - Load error
  - `isSubmitting: Boolean` - Update in progress
  - `submitError: String?` - Update error
- **Behavior**:
  - Loads existing COT via `ApiClient.getCot(id)`
  - Pre-populates `CotEditor` with current name/dslCode
  - Submits to `ApiClient.updateCot(id, ...)`
  - On success: Shows toast + navigates to `/cots/{id}`
  - On failure: Shows error panel + toast
  - Cancel navigates to `/cots/{id}`
- **Note**: Handles both load and submit errors separately

### GeneratePage
Generate output from template with dynamic parameter form.

```kotlin
@Page("/cots/generate/{id}")
@Composable
fun GeneratePage()
```

- **Route**: `/cots/generate/{id}` (dynamic ID parameter)
- **State**:
  - `cot: CotDetailResponse?` - Template definition
  - `loading: Boolean` - Initial load
  - `loadError: String?` - Load error
  - `isGenerating: Boolean` - Generation in progress
  - `generateError: String?` - Generation error
  - `generatedOutput: String?` - Result output
  - `parameters: Map<String, String>` - User-entered parameters
  - `copySuccess: Boolean` - Clipboard copy feedback
- **Behavior**:
  - Loads COT definition
  - Extracts parameter names from DSL code (via `extractParametersFromDsl`)
  - Renders dynamic input form (one text field per parameter)
  - Generate button calls `ApiClient.generateOutput(id, parameters)`
  - Displays generated output in styled panel
  - Copy-to-clipboard button with success feedback
  - Breadcrumb back to `/cots/{id}`

**Key Helper Function:**
```kotlin
private fun extractParametersFromDsl(dslCode: String): Map<String, String>
```
Parses DSL code to find parameter references (e.g., `Params.name`, `Params.age`) and initializes them with empty strings.

## Route Parameters

Pages use Kobweb's route parameter system:

```kotlin
val ctx = rememberPageContext()
val id = ctx.route.params["id"] ?: ""
```

- `{id}` in route path becomes accessible via `ctx.route.params["id"]`
- Missing parameters default to empty string
- Used for fetching specific COT by ID

## State Management Pattern

All COT pages follow a consistent pattern:

**Load Phase:**
```kotlin
var data by remember { mutableStateOf<T?>(null) }
var loading by remember { mutableStateOf(true) }
var error by remember { mutableStateOf<String?>(null) }

LaunchedEffect(id) {
    loading = true
    ApiClient.getData(id)
        .onSuccess { data = it; loading = false }
        .onFailure { error = it.message; loading = false }
}
```

**Submit Phase** (Create/Edit):
```kotlin
var isSubmitting by remember { mutableStateOf(false) }
var submitError by remember { mutableStateOf<String?>(null) }

onSubmit = { name, dslCode ->
    isSubmitting = true
    submitError = null
    
    ApiClient.submitData(...)
        .onSuccess { 
            ToastManager.showSuccess("Success!")
            ctx.router.navigateTo("/next-page")
        }
        .onFailure {
            submitError = it.message
            ToastManager.showError(it.message ?: "Failed")
            isSubmitting = false
        }
}
```

## Navigation Flows

### Creation Flow
1. User clicks "Create" on Templates page
2. `/cots/create` → Shows CotEditor
3. User submits → Creates COT
4. Redirects to `/cots/{newId}` (detail page)

### Edit Flow
1. User views COT at `/cots/{id}`
2. Clicks "Edit" → `/cots/edit/{id}`
3. Loads existing data → Pre-populates form
4. User submits → Updates COT
5. Redirects to `/cots/{id}` (detail page)

### Generate Flow
1. User views COT at `/cots/{id}`
2. Clicks "Generate" → `/cots/generate/{id}`
3. Extracts parameters → Renders input form
4. User fills parameters + generates
5. Shows output + copy button
6. Can regenerate with different parameters

### Delete Flow
1. User views COT at `/cots/{id}`
2. Clicks "Delete" → Shows confirmation
3. User confirms → Deletes COT
4. Redirects to `/templates` (list page)

## Error Handling

### Load Errors
Display error panel if COT cannot be loaded:
- Show error message
- Provide link back to templates list
- Do not crash or show incomplete data

### Submit Errors
Display both in-page error panel AND toast:
- Error panel: Shows detailed message
- Toast: Provides immediate feedback
- Re-enable form for retry

### 404 / Not Found
If COT ID doesn't exist, API returns failure:
- Show "COT not found" error
- Provide navigation back to list

## Parameter Extraction Algorithm

The `extractParametersFromDsl` function:

1. Searches DSL code for `Params.{name}` pattern
2. Uses regex to find all parameter references
3. Returns Map with parameter names as keys, empty strings as initial values
4. Example: `Params.name` and `Params.age` → `{"name": "", "age": ""}`

**Note**: This is client-side heuristic parsing, not true DSL parsing.

## Rules / Constraints

1. **ID Required**: Detail/Edit/Generate pages require valid ID in route
2. **Load Before Edit**: Edit page must fetch current data before allowing changes
3. **Optimistic Navigation**: Navigate on success, stay on page on failure
4. **Toast Feedback**: Always show toast on successful mutation
5. **Confirmation for Delete**: Require user confirmation before destructive action
6. **Breadcrumbs**: Always provide navigation back to parent page

## Extending / Adding Features

### Adding New COT Action Page

Example: Export page at `/cots/export/{id}`:

```kotlin
package com.niloda.pages.cots

@Page("/cots/export/{id}")
@Composable
fun ExportCotPage() {
    val ctx = rememberPageContext()
    val id = ctx.route.params["id"] ?: ""
    
    var cot by remember { mutableStateOf<CotDetailResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    
    LaunchedEffect(id) {
        ApiClient.getCot(id)
            .onSuccess { cot = it; loading = false }
            .onFailure { /* handle error */ }
    }
    
    PageLayout("Export COT") {
        // Export UI
    }
}
```

### Adding Parameter Validation

Before generating, validate parameters:

```kotlin
fun validateParameters(params: Map<String, String>): List<String> {
    val errors = mutableListOf<String>()
    
    params.forEach { (name, value) ->
        if (value.isBlank()) {
            errors.add("Parameter '$name' is required")
        }
    }
    
    return errors
}

// Use in Generate page
if (validateParameters(parameters).isEmpty()) {
    // Proceed with generation
}
```

## Dependencies
- **Kobweb Core** - `@Page`, routing, `rememberPageContext`
- **Compose Runtime** - State management, effects
- **com.niloda.api** - API client for backend communication
- **com.niloda.components** - CotEditor, PageLayout, LoadingSpinner, ToastManager
- **kotlinx.serialization** - JSON parameter handling
- **Browser API** - Clipboard access (`window.navigator.clipboard`)

## Testing Considerations

When testing COT pages:
1. **Mock API responses**: Test success/failure states
2. **Route parameters**: Verify ID extraction and usage
3. **Navigation**: Verify correct redirects
4. **State transitions**: Test loading → success → editing → submitting flows
5. **Error recovery**: Ensure errors don't break page state
6. **Parameter extraction**: Test various DSL patterns
