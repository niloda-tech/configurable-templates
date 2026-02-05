package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*

/**
 * A reusable component for editing COT DSL code
 * Provides:
 * - Name input field
 * - Code editor with syntax highlighting
 * - Client-side validation
 * - Submit and cancel actions
 * 
 * This is the stateful container that manages state and side effects.
 */
@Composable
fun CotEditor(
    initialName: String = "",
    initialDslCode: String = "",
    submitButtonText: String = "Create",
    onSubmit: (name: String, dslCode: String) -> Unit,
    onCancel: () -> Unit,
    isSubmitting: Boolean = false
) {
    var name by remember { mutableStateOf(initialName) }
    var dslCode by remember { mutableStateOf(initialDslCode) }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // Validate on change
    LaunchedEffect(name, dslCode) {
        validationErrors = validateCotInput(name, dslCode)
    }
    
    // Keyboard shortcuts
    DisposableEffect(Unit) {
        val handleKeyDown = { event: dynamic ->
            val e = event as org.w3c.dom.events.KeyboardEvent
            
            // Ctrl+S or Cmd+S to save
            if ((e.ctrlKey || e.metaKey) && e.key == "s") {
                e.preventDefault()
                if (!isSubmitting && validationErrors.isEmpty()) {
                    onSubmit(name, dslCode)
                }
            }
            
            // Escape to cancel
            if (e.key == "Escape") {
                e.preventDefault()
                if (!isSubmitting) {
                    onCancel()
                }
            }
        }
        
        kotlinx.browser.window.addEventListener("keydown", handleKeyDown)
        
        onDispose {
            kotlinx.browser.window.removeEventListener("keydown", handleKeyDown)
        }
    }
    
    CotEditorContent(
        name = name,
        dslCode = dslCode,
        validationErrors = validationErrors,
        isSubmitting = isSubmitting,
        submitButtonText = submitButtonText,
        onNameChange = { name = it },
        onDslCodeChange = { dslCode = it },
        onSubmit = { onSubmit(name, dslCode) },
        onCancel = onCancel
    )
}

/**
 * Stateless content composable for CotEditor
 * Renders the UI layout and delegates to section composables
 */
@Composable
private fun CotEditorContent(
    name: String,
    dslCode: String,
    validationErrors: List<String>,
    isSubmitting: Boolean,
    submitButtonText: String,
    onNameChange: (String) -> Unit,
    onDslCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .gap(1.5.em)
    ) {
        CotNameSection(
            name = name,
            isSubmitting = isSubmitting,
            onNameChange = onNameChange
        )
        
        CotDslSection(
            dslCode = dslCode,
            isSubmitting = isSubmitting,
            onDslCodeChange = onDslCodeChange
        )
        
        if (validationErrors.isNotEmpty()) {
            CotValidationErrorsPanel(errors = validationErrors)
        }
        
        CotActionButtons(
            submitButtonText = submitButtonText,
            isSubmitting = isSubmitting,
            hasValidationErrors = validationErrors.isNotEmpty(),
            onSubmit = onSubmit,
            onCancel = onCancel
        )
    }
}

// Regex pattern for template name validation (compiled once)
private val TEMPLATE_NAME_PATTERN = Regex("^[A-Za-z][A-Za-z0-9_]*$")

/**
 * Client-side validation for COT input
 * Returns list of validation errors
 */
private fun validateCotInput(name: String, dslCode: String): List<String> {
    val errors = mutableListOf<String>()
    
    // Validate name
    if (name.isBlank()) {
        errors.add("Template name is required")
    } else if (name.length < 3) {
        errors.add("Template name must be at least 3 characters")
    } else if (!name.matches(TEMPLATE_NAME_PATTERN)) {
        errors.add("Template name must start with a letter and contain only letters, numbers, and underscores")
    }
    
    // Validate DSL code
    if (dslCode.isBlank()) {
        errors.add("DSL code is required")
    } else {
        // Basic DSL syntax validation
        if (!dslCode.trim().startsWith("cot(")) {
            errors.add("DSL code must start with 'cot('")
        }
        
        // Check for balanced braces and parentheses in a single pass
        var openBraces = 0
        var closeBraces = 0
        var openParens = 0
        var closeParens = 0
        
        dslCode.forEach { char ->
            when (char) {
                '{' -> openBraces++
                '}' -> closeBraces++
                '(' -> openParens++
                ')' -> closeParens++
            }
        }
        
        if (openBraces != closeBraces) {
            errors.add("Unbalanced braces: $openBraces opening '{' but $closeBraces closing '}'")
        }
        
        if (openParens != closeParens) {
            errors.add("Unbalanced parentheses: $openParens opening '(' but $closeParens closing ')'")
        }
    }
    
    return errors
}
