# Package: com.niloda.pages

## Purpose
Top-level Kobweb page routes for the COT Editor application. Contains composable functions annotated with `@Page` that map to URL routes and render the main views.

## Key Files
- **Index.kt** - Home/welcome page at root path (`/`)
- **Templates.kt** - COT list/browse page at `/templates`
- **About.kt** - About/info page at `/about`
- **cots/** - Sub-package with COT-specific pages (Create, Edit, View, Generate)

## Public API / Entry Points

### HomePage
Welcome landing page explaining the application.

```kotlin
@Page
@Composable
fun HomePage()
```

- **Route**: `/` (root)
- **Content**: 
  - Welcome message and description
  - Feature list
  - Link to Templates page
- **Dependencies**: None (static content)

### TemplatesPage
Browse all COT templates with create button.

```kotlin
@Page
@Composable
fun TemplatesPage()
```

- **Route**: `/templates`
- **State**:
  - `cots: List<CotSummary>` - List of templates from API
  - `loading: Boolean` - Loading state
  - `error: String?` - Error message if load fails
- **Behavior**:
  - Fetches COTs on mount via `LaunchedEffect(Unit)`
  - Shows LoadingSpinner while loading
  - Shows error panel if fetch fails
  - Renders list of CotCard components (clickable cards)
  - Create button navigates to `/cots/create`
- **Navigation**: Cards link to `/cots/{id}` detail pages

### AboutPage
Information about the COT Editor.

```kotlin
@Page
@Composable
fun AboutPage()
```

- **Route**: `/about`
- **Content**: Static about/info content (implementation varies)

## Page Structure Pattern

All pages follow this structure:

```kotlin
@Page
@Composable
fun SomePage() {
    val ctx = rememberPageContext()  // For navigation
    var state by remember { mutableStateOf(...) }
    
    // Load data on mount
    LaunchedEffect(Unit) {
        // Fetch from API
    }
    
    PageLayout("Page Title") {
        // Page content
    }
}
```

## Navigation

Pages use Kobweb's navigation APIs:

```kotlin
val ctx = rememberPageContext()

// Navigate programmatically
ctx.router.navigateTo("/path")

// In components, use Link
Link("/path", "Link Text")
```

## Data Loading Pattern

Pages that fetch data use this pattern:

```kotlin
var data by remember { mutableStateOf<T?>(null) }
var loading by remember { mutableStateOf(true) }
var error by remember { mutableStateOf<String?>(null) }

LaunchedEffect(Unit) {
    loading = true
    error = null
    
    ApiClient.someMethod()
        .onSuccess { response ->
            data = response
            loading = false
        }
        .onFailure { e ->
            error = e.message ?: "Unknown error"
            loading = false
        }
}

// Render based on state
when {
    loading -> LoadingSpinner()
    error != null -> ErrorPanel(error)
    data != null -> Content(data)
}
```

## Sub-Package: cots

The `com.niloda.pages.cots` package contains COT-specific CRUD pages:

- **Create.kt** (`/cots/create`) - Create new COT template
- **Edit.kt** (`/cots/edit`) - Edit existing COT (ID in query param)
- **[id].kt** (`/cots/{id}`) - View COT details
- **Generate.kt** (`/cots/{id}/generate`) - Generate output from template

See `com.niloda.pages.cots.LLM.md` for details on these pages.

## Rules / Constraints

1. **Pages are Entry Points** - They orchestrate components and API calls
2. **One Route per Page** - Each `@Page` function maps to exactly one URL
3. **Use PageLayout** - All pages should wrap content in `PageLayout` for consistency
4. **Handle All States** - Pages must handle loading, error, and success states
5. **No Business Logic** - Complex logic belongs in components or utilities
6. **Toast for Feedback** - Use ToastManager for user feedback on actions

## Error Handling

Pages handle errors at the boundary:

```kotlin
ApiClient.someMethod()
    .onFailure { e ->
        // Option 1: Store in state and show error panel
        error = e.message
        
        // Option 2: Show toast notification
        ToastManager.showError(e.message ?: "Operation failed")
        
        // Option 3: Both
    }
```

## State Management

Pages use simple local state with `remember`:

- **Lightweight state**: `var x by remember { mutableStateOf(...) }`
- **Derived state**: Use `remember(deps) { ... }` for computed values
- **Effects**: `LaunchedEffect` for side effects (API calls)
- **No global state**: Each page manages its own state

## Extending / Adding New Pages

To add a new page:

1. **Create file** in `com.niloda.pages/`:
```kotlin
package com.niloda.pages

import com.varabyte.kobweb.core.Page
import androidx.compose.runtime.*

@Page
@Composable
fun NewPage() {
    PageLayout("New Page") {
        // Content
    }
}
```

2. **Route**: File name determines route:
   - `NewPage.kt` → `/newpage`
   - `SomeFeature.kt` → `/somefeature`
   - `nested/Detail.kt` → `/nested/detail`

3. **Follow patterns**:
   - Use PageLayout wrapper
   - Handle loading/error states
   - Use ApiClient for data
   - Use ToastManager for feedback
   - Navigate with `ctx.router.navigateTo(...)`

## Dependencies
- **Kobweb Core** - `@Page`, `rememberPageContext`, routing
- **Kobweb Silk** - Link component, navigation helpers
- **Compose Runtime** - State management
- **com.niloda.api** - ApiClient for backend calls
- **com.niloda.components** - Reusable UI components (PageLayout, LoadingSpinner, Toast)

## Related Documentation
- See `com.niloda.pages.cots.LLM.md` for COT-specific pages
- See `com.niloda.components.LLM.md` for component details
- See `com.niloda.api.LLM.md` for API client usage
