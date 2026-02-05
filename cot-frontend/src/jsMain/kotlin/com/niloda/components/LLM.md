# Package: com.niloda.components

## Purpose
Reusable UI components for the COT Editor frontend. Provides composable building blocks for the application including form editors, navigation, loading states, and toast notifications.

## Key Files
- **CotEditor.kt** - Primary template editor component with validation and keyboard shortcuts
- **Toast.kt** - Global toast notification system with manager and container
- **PageLayout.kt** - Shared page layout with navigation header
- **LoadingSpinner.kt** - Loading state indicator component

## Public API / Entry Points

### CotEditor Component
Main form component for creating/editing COT templates.

```kotlin
@Composable
fun CotEditor(
    initialName: String = "",
    initialDslCode: String = "",
    submitButtonText: String = "Create",
    onSubmit: (name: String, dslCode: String) -> Unit,
    onCancel: () -> Unit,
    isSubmitting: Boolean = false
)
```

**Features:**
- Live client-side validation (displays errors inline)
- Keyboard shortcuts: Ctrl/Cmd+S to save, Escape to cancel
- Name input field with placeholder
- DSL code editor (textarea with monospace styling, dark theme)
- Validation error panel (shows when errors exist)
- Submit/Cancel action buttons (disabled during submission or validation errors)

**Validation Rules** (implemented in `validateCotInput`):
- Name must not be blank
- Name must be at least 3 characters
- Name must match pattern: `^[A-Za-z][A-Za-z0-9_]*$` (letter start, alphanumeric + underscore)
- DSL code must not be blank
- DSL code must start with `cot(`
- Balanced braces `{}`
- Balanced parentheses `()`

### Toast System

**ToastManager Object** - Global singleton for managing toast notifications:
```kotlin
object ToastManager {
    val toasts: List<ToastMessage>
    fun showToast(message: String, type: ToastType, duration: Long)
    fun showSuccess(message: String, duration: Long = 3000L)
    fun showError(message: String, duration: Long = 5000L)
    fun showInfo(message: String, duration: Long = 3000L)
    fun showWarning(message: String, duration: Long = 4000L)
    fun removeToast(id: String)
}
```

**ToastContainer Component** - Renders all active toasts:
```kotlin
@Composable
fun ToastContainer()
```
- Should be placed once at app root (in `AppEntry`)
- Fixed position (top-right corner)
- Auto-dismisses after duration
- Manual dismiss via X button
- Slide-in animation from right

**Toast Types:**
- `SUCCESS` - Green, checkmark icon, 3s default
- `ERROR` - Red, X icon, 5s default
- `WARNING` - Yellow, warning icon, 4s default
- `INFO` - Blue, info icon, 3s default

**Setup Required:**
Call `injectToastStyles()` once during app initialization to add CSS keyframes.

### PageLayout Component
Shared layout wrapper for all pages with navigation.

```kotlin
@Composable
fun PageLayout(title: String, content: @Composable () -> Unit)
```

**Features:**
- Desktop/mobile responsive navigation
- Links: Home (/), COTs (/templates), About (/about)
- Mobile hamburger menu toggle
- Page title rendering
- Consistent spacing and padding

### LoadingSpinner Component
Generic loading indicator.

```kotlin
@Composable
fun LoadingSpinner(
    text: String = "Loading...",
    modifier: Modifier = Modifier
)
```

**Setup Required:**
Call `injectSpinnerStyles()` once during app initialization to add CSS animation.

## Data Contracts / Invariants

1. **CotEditor State Management**:
   - Component owns state (`name`, `dslCode`, `validationErrors`)
   - Validation runs reactively via `LaunchedEffect(name, dslCode)`
   - Keyboard listener added/removed via `DisposableEffect`

2. **Toast State**:
   - ToastManager uses `mutableStateListOf` for reactive updates
   - Each toast has unique auto-generated `id`
   - Toasts auto-remove after their `duration` via coroutine delay

3. **Validation**:
   - `TEMPLATE_NAME_PATTERN` is compiled once (module-level `val`)
   - Validation function is pure (no side effects)
   - Returns `List<String>` of error messages (empty list = valid)

## Styling Patterns

1. **Color Scheme**:
   - Primary: `rgb(59, 130, 246)` (blue)
   - Success: `rgb(34, 197, 94)` (green)
   - Error: `rgb(220, 38, 38)` (red)
   - Warning: `rgb(234, 179, 8)` (yellow)
   - Text: `rgb(31, 41, 55)` (dark gray)

2. **Common Patterns**:
   - Border radius: `0.375em`
   - Padding: `1em`, `0.75em`
   - Gap between elements: `1em`, `0.5em`
   - Transitions: `0.2s` for hover effects

3. **Responsive Design**:
   - Mobile: `max-width: 768px`
   - Tablet: `769px - 1024px`
   - Desktop: `> 1024px`

## Side Effects / Lifecycle

### CotEditor Keyboard Listeners
- **Registration**: `DisposableEffect(Unit)` adds global `keydown` listener
- **Cleanup**: `onDispose` removes listener when component unmounts
- **Prevents double-registration** by using `Unit` key (only registers once)

### Toast Auto-Dismiss
- Each toast uses `LaunchedEffect(toast.id)` to schedule removal
- Coroutine launched per toast, delays for `toast.duration`, then removes
- Effect keyed by `toast.id` prevents re-triggering

## Rules / Constraints

1. **Components are Stateful** (not pure) - They use `remember` and manage local state
2. **No API Calls** - Components receive callbacks (`onSubmit`, `onCancel`) from pages
3. **No Navigation** - Pages handle navigation, components just trigger callbacks
4. **Validation is Client-Side Only** - Server may have additional validation
5. **Toast Manager is Global Singleton** - Single source of truth for all toasts

## Extending / Customization

### Adding Validation Rules to CotEditor

Modify `validateCotInput` function:
```kotlin
private fun validateCotInput(name: String, dslCode: String): List<String> {
    val errors = mutableListOf<String>()
    
    // Add new validation
    if (name.length > 50) {
        errors.add("Template name must be 50 characters or less")
    }
    
    // ... existing validation
    return errors
}
```

### Creating New Toast Type

1. Add enum value:
```kotlin
enum class ToastType {
    SUCCESS, ERROR, INFO, WARNING, CUSTOM
}
```

2. Add styling in `Toast` composable:
```kotlin
val (bgColor, borderColor, textColor, icon) = when (toast.type) {
    // ... existing types
    ToastType.CUSTOM -> listOf("...", "...", "...", "🎉")
}
```

3. Add convenience method:
```kotlin
fun showCustom(message: String, duration: Long = 4000L) {
    showToast(message, ToastType.CUSTOM, duration)
}
```

### Adding New Component

Follow these patterns:
1. Mark with `@Composable` annotation
2. Use Kobweb/Compose HTML primitives (Column, Row, Box, Modifier)
3. Accept parameters for configuration
4. Accept callback lambdas for actions
5. Keep component focused on single responsibility
6. Use existing color/spacing constants for consistency

## Dependencies
- **Compose Runtime** - State management (`remember`, `LaunchedEffect`, etc.)
- **Kobweb Compose** - Layout primitives (Column, Row, Box, Modifier)
- **Kobweb Silk** - Navigation (Link) and text components
- **Compose Web** - HTML DOM elements (Input, TextArea, Button, Div, etc.)
- **kotlinx.coroutines** - Async operations for toast auto-dismiss
- **Browser API** - `window.addEventListener` for keyboard shortcuts

## Testing Considerations

When testing components:
1. **Validation Logic**: Test `validateCotInput` with various inputs
2. **State Updates**: Verify reactive validation triggers
3. **Keyboard Shortcuts**: Simulate keydown events (Ctrl+S, Escape)
4. **Toast Lifecycle**: Verify auto-dismiss timing and manual removal
5. **Responsive Behavior**: Test at different viewport widths
