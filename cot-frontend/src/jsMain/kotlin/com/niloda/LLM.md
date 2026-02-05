# Package: com.niloda (Root Package)

## Purpose
Application entry point and global initialization for the COT Editor frontend. Contains the main `@App` composable and style initialization logic that runs once when the application starts.

## Key Files
- **AppEntry.kt** - Main application entry point with `@App` annotation

## Public API / Entry Points

### AppEntry Function
Main application composable that wraps all pages and sets up global features.

```kotlin
@App
@Composable
fun AppEntry(content: @Composable () -> Unit)
```

- **Annotation**: `@App` - Marks this as the Kobweb application root
- **Parameter**: `content` - Injected page content (Kobweb router provides this)
- **Wraps**:
  - `SilkApp` - Kobweb Silk framework wrapper
  - `Surface` - Root surface with smooth color transitions
  - `ToastContainer` - Global toast notification container
- **Styling**: Sets minimum height to 100vh (full viewport)

**Structure:**
```kotlin
SilkApp {
    Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
        content()           // Page router content
        ToastContainer()    // Global toast overlay
    }
}
```

### initStyles Function
Initialization hook for global styles and CSS injections.

```kotlin
@InitSilk
fun initStyles(ctx: InitSilkContext)
```

- **Annotation**: `@InitSilk` - Runs once before app starts
- **Responsibilities**:
  1. Register base styles (smooth scroll behavior)
  2. Inject toast animation CSS
  3. Inject spinner animation CSS
  4. Inject responsive/mobile CSS

**Called During**: Application bootstrap (before any composables render)

### injectResponsiveStyles Function
Injects CSS for responsive mobile/tablet/desktop behavior.

```kotlin
fun injectResponsiveStyles()
```

**CSS Rules Injected:**
- Desktop (default):
  - `.desktop-nav` → `display: flex`
  - `.mobile-menu-button` → `display: none`

- Mobile (`max-width: 768px`):
  - `.desktop-nav` → `display: none`
  - `.mobile-menu-button` → `display: block`
  - Full-width buttons
  - Reduced padding (1em)

- Tablet (`769px - 1024px`):
  - Reduced padding (1.5em)

## Initialization Sequence

1. **Kobweb Bootstrap** - Framework initialization
2. **@InitSilk Functions** - `initStyles()` called
   - Registers smooth scroll
   - Calls `injectToastStyles()`
   - Calls `injectSpinnerStyles()`
   - Calls `injectResponsiveStyles()`
3. **@App Composable** - `AppEntry()` rendered
   - Sets up SilkApp context
   - Renders Surface wrapper
   - Mounts ToastContainer
   - Router injects page content
4. **Page Rendering** - First page composable renders

## Global Features

### Toast System Integration
`ToastContainer()` is rendered once at app root:
- Fixed position overlay (top-right)
- z-index 9999 (above all content)
- Observes `ToastManager.toasts` globally
- Any page can call `ToastManager.showToast(...)`

### Responsive Design
Breakpoints managed via injected CSS:
- **Mobile**: ≤768px
- **Tablet**: 769-1024px
- **Desktop**: ≥1025px

Classes `.desktop-nav` and `.mobile-menu-button` control navigation visibility.

## Style Registration

### Smooth Scroll
Applied to `body` element:
```kotlin
ctx.stylesheet.registerStyleBase("body") { 
    Modifier.scrollBehavior(ScrollBehavior.Smooth) 
}
```

Enables smooth scrolling for anchor links and programmatic scrolling.

### Animation Keyframes
CSS animations defined via `<style>` elements:
- **@keyframes spin** - 360° rotation for LoadingSpinner
- **@keyframes slideInRight** - Toast entrance animation

Injected once into `document.head` during initialization.

## Rules / Constraints

1. **Single App Entry Point** - Only one `@App` function per application
2. **InitSilk Runs Once** - Initialization logic must be idempotent
3. **Style Injection is Global** - CSS affects entire document
4. **ToastContainer is Singleton** - Only render once (at app root)
5. **No Page Logic** - This package only handles app-wide setup

## Lifecycle

```
Application Start
    ↓
@InitSilk functions run
    ↓
CSS injected into <head>
    ↓
@App composable mounted
    ↓
Kobweb router active
    ↓
Pages render as needed
```

**Note**: `@InitSilk` runs before any `@Composable`, ensuring styles are ready before first render.

## Extending / Customization

### Adding Global Style

Add to `initStyles`:
```kotlin
@InitSilk
fun initStyles(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        registerStyleBase("body") { /* ... */ }
        registerStyleBase("input") { /* New global input styles */ }
    }
    // ... existing injections
}
```

### Adding Global Component

Add to `AppEntry`:
```kotlin
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Surface(...) {
            content()
            ToastContainer()
            NewGlobalComponent()  // Add here
        }
    }
}
```

### Customizing Breakpoints

Modify `injectResponsiveStyles`:
```kotlin
@media (max-width: 600px) {  // Change from 768px
    /* Mobile styles */
}
```

### Adding Dark Mode Support

1. Create color mode styles:
```kotlin
@InitSilk
fun initStyles(ctx: InitSilkContext) {
    ctx.theme.palettes.light.background = Colors.White
    ctx.theme.palettes.dark.background = Colors.Black
    // ... more colors
}
```

2. Use `ColorMode.current` in components:
```kotlin
val colorMode = ColorMode.current
val bgColor = if (colorMode.isDark) Colors.Black else Colors.White
```

## Dependencies
- **Kobweb Core** - `@App` annotation, application framework
- **Kobweb Silk** - `SilkApp`, `InitSilk`, `Surface`, `ColorMode`
- **Compose Runtime** - `@Composable`, composition framework
- **com.niloda.components** - `ToastContainer`, `injectToastStyles()`, `injectSpinnerStyles()`
- **Browser API** - `document.createElement()`, `document.head`

## Configuration

### Kobweb Configuration (.kobweb/conf.yaml)
App configuration is in `.kobweb/conf.yaml`:
```yaml
site:
  title: "COT Editor"
  
server:
  port: 8081
  files:
    dev:
      contentRoot: "build/processedResources/js/main/public"
      script: "build/kotlin-webpack/js/developmentExecutable/cot-frontend.js"
```

### Build Configuration (build.gradle.kts)
Uses `configAsKobwebApplication`:
```kotlin
kotlin {
    configAsKobwebApplication("cot-frontend")
}
```

## Important Notes

1. **Do Not Add Business Logic** - This package is for setup only
2. **CSS Injection is Manual** - Kobweb doesn't use CSS-in-JS bundlers, we inject `<style>` tags
3. **Color Mode** - Default is light mode; dark mode support requires explicit implementation
4. **Performance**: Style injection is synchronous during initialization (acceptable for small stylesheets)

## Related Documentation
- See `com.niloda.components.LLM.md` for ToastContainer details
- See `com.niloda.pages.LLM.md` for page routing
- See Kobweb docs: https://kobweb.varabyte.com/
