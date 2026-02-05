package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

/**
 * A reusable component for editing COT DSL code
 * Provides:
 * - Name input field
 * - Code editor with syntax highlighting
 * - Client-side validation
 * - Submit and cancel actions
 *
 * Container/Stateful Component:
 * - Manages state (name, dslCode, validationErrors)
 * - Handles side-effects (validation, keyboard shortcuts)
 * - Delegates rendering to CotEditorContent
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
        onNameChange = { name = it },
        dslCode = dslCode,
        onDslCodeChange = { dslCode = it },
        validationErrors = validationErrors,
        submitButtonText = submitButtonText,
        isSubmitting = isSubmitting,
        onSubmit = { onSubmit(name, dslCode) },
        onCancel = onCancel
    )
}

/**
 * Stateless/Content Component:
 * - Pure rendering based on parameters
 * - No state management or side-effects
 * - Composes sections into complete editor UI
 */
@Composable
private fun CotEditorContent(
    name: String,
    onNameChange: (String) -> Unit,
    dslCode: String,
    onDslCodeChange: (String) -> Unit,
    validationErrors: List<String>,
    submitButtonText: String,
    isSubmitting: Boolean,
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
            onNameChange = onNameChange,
            isSubmitting = isSubmitting
        )
        
        CotDslSection(
            dslCode = dslCode,
            onDslCodeChange = onDslCodeChange,
            isSubmitting = isSubmitting
        )
        
        CotValidationErrorsPanel(
            errors = validationErrors
        )
        
        CotActionButtons(
            submitButtonText = submitButtonText,
            isSubmitting = isSubmitting,
            hasValidationErrors = validationErrors.isNotEmpty(),
            onSubmit = onSubmit,
            onCancel = onCancel
        )
    }
}

/**
 * Section: Template Name Input
 * - Focused composable for name input field
 * - Self-contained with label and input styling
 */
@Composable
private fun CotNameSection(
    name: String,
    onNameChange: (String) -> Unit,
    isSubmitting: Boolean
) {
    Column(modifier = Modifier.gap(0.5.em)) {
        Label(forId = "cot-name") {
            SpanText(
                "Template Name",
                modifier = Modifier
                    .fontSize(1.1.em)
                    .fontWeight(600)
                    .color(rgb(31, 41, 55))
            )
        }
        
        Input(
            type = InputType.Text,
            attrs = {
                id("cot-name")
                value(name)
                onInput { onNameChange(it.value) }
                placeholder("Enter template name (e.g., GreetingTemplate)")
                if (isSubmitting) {
                    attr("disabled", "")
                }
                style {
                    property("width", "100%")
                    property("padding", "0.75em")
                    property("border", "1px solid rgb(209, 213, 219)")
                    property("border-radius", "0.375em")
                    property("font-size", "1em")
                    property("font-family", "inherit")
                }
            }
        )
    }
}

/**
 * Section: DSL Code Editor
 * - Focused composable for DSL code textarea
 * - Includes label with badge, textarea, and helper text
 */
@Composable
private fun CotDslSection(
    dslCode: String,
    onDslCodeChange: (String) -> Unit,
    isSubmitting: Boolean
) {
    Column(modifier = Modifier.gap(0.5.em)) {
        Row(
            modifier = Modifier.fillMaxWidth().gap(0.5.em),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Label(forId = "cot-dsl") {
                SpanText(
                    "DSL Code",
                    modifier = Modifier
                        .fontSize(1.1.em)
                        .fontWeight(600)
                        .color(rgb(31, 41, 55))
                )
            }
            
            SpanText(
                "Kotlin DSL",
                modifier = Modifier
                    .fontSize(0.85.em)
                    .color(rgb(107, 114, 128))
                    .padding(leftRight = 0.5.em, topBottom = 0.25.em)
                    .backgroundColor(rgb(243, 244, 246))
                    .borderRadius(0.25.em)
            )
        }
        
        TextArea(
            value = dslCode,
            attrs = {
                id("cot-dsl")
                onInput { onDslCodeChange(it.value) }
                if (isSubmitting) {
                    attr("disabled", "")
                }
                attr("rows", "15")
                style {
                    property("width", "100%")
                    property("padding", "1em")
                    property("border", "1px solid rgb(209, 213, 219)")
                    property("border-radius", "0.375em")
                    property("font-family", "monospace")
                    property("font-size", "0.95em")
                    property("line-height", "1.5")
                    property("background-color", "rgb(17, 24, 39)")
                    property("color", "rgb(229, 231, 235)")
                    property("resize", "vertical")
                    property("tab-size", "4")
                }
            }
        )
        
        // Syntax hint
        SpanText(
            "Example: cot(\"MyTemplate\") { \"Hello\".text }",
            modifier = Modifier
                .fontSize(0.85.em)
                .color(rgb(107, 114, 128))
                .fontFamily("monospace")
        )
        
        // Keyboard shortcuts hint
        SpanText(
            "💡 Tip: Press Ctrl+S (or Cmd+S) to save, Escape to cancel",
            modifier = Modifier
                .fontSize(0.85.em)
                .color(rgb(107, 114, 128))
                .attrsModifier {
                    style {
                        property("font-style", "italic")
                    }
                }
        )
    }
}

/**
 * Section: Validation Errors Panel
 * - Displays validation errors in a styled error panel
 * - Only renders when errors exist
 */
@Composable
private fun CotValidationErrorsPanel(
    errors: List<String>
) {
    if (errors.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.em)
                .backgroundColor(rgb(254, 242, 242))
                .border(1.px, LineStyle.Solid, rgb(220, 38, 38))
                .borderRadius(0.375.em)
                .gap(0.5.em)
        ) {
            SpanText(
                "⚠ Validation Errors:",
                modifier = Modifier
                    .fontSize(1.em)
                    .fontWeight(600)
                    .color(rgb(220, 38, 38))
            )
            
            errors.forEach { error ->
                SpanText(
                    "• $error",
                    modifier = Modifier
                        .fontSize(0.95.em)
                        .color(rgb(127, 29, 29))
                )
            }
        }
    }
}

/**
 * Section: Action Buttons (Submit & Cancel)
 * - Self-contained button row with submit and cancel actions
 * - Handles disabled states and hover effects
 */
@Composable
private fun CotActionButtons(
    submitButtonText: String,
    isSubmitting: Boolean,
    hasValidationErrors: Boolean,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Row(modifier = Modifier.gap(1.em)) {
        // Submit Button
        Button(
            attrs = {
                onClick {
                    if (!isSubmitting && !hasValidationErrors) {
                        onSubmit()
                    }
                }
                if (isSubmitting || hasValidationErrors) {
                    attr("disabled", "")
                }
                style {
                    property("padding", "0.75em 1.5em")
                    property("background-color", 
                        if (isSubmitting || hasValidationErrors) 
                            "rgb(156, 163, 175)" 
                        else 
                            "rgb(59, 130, 246)"
                    )
                    property("color", "white")
                    property("border", "none")
                    property("border-radius", "0.375em")
                    property("cursor", 
                        if (isSubmitting || hasValidationErrors) 
                            "not-allowed" 
                        else 
                            "pointer"
                    )
                    property("font-size", "1em")
                    property("font-weight", "600")
                    property("transition", "background-color 0.2s")
                }
                if (!isSubmitting && !hasValidationErrors) {
                    onMouseOver { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(37, 99, 235)"
                    }
                    onMouseOut { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(59, 130, 246)"
                    }
                }
            }
        ) {
            Text(if (isSubmitting) "Submitting..." else submitButtonText)
        }
        
        // Cancel Button
        Button(
            attrs = {
                onClick { if (!isSubmitting) onCancel() }
                if (isSubmitting) {
                    attr("disabled", "")
                }
                style {
                    property("padding", "0.75em 1.5em")
                    property("background-color", "rgb(243, 244, 246)")
                    property("color", "rgb(31, 41, 55)")
                    property("border", "1px solid rgb(209, 213, 219)")
                    property("border-radius", "0.375em")
                    property("cursor", if (isSubmitting) "not-allowed" else "pointer")
                    property("font-size", "1em")
                    property("font-weight", "600")
                    property("transition", "background-color 0.2s")
                }
                if (!isSubmitting) {
                    onMouseOver { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(229, 231, 235)"
                    }
                    onMouseOut { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(243, 244, 246)"
                    }
                }
            }
        ) {
            Text("Cancel")
        }
    }
}

// Regex pattern for template name validation (compiled once)
private val TEMPLATE_NAME_PATTERN = Regex("^[A-Za-z][A-Za-z0-9_]*$")

/**
 * Client-side validation for COT input
 * Returns list of validation errors
 * 
 * Validation logic is kept separate and reusable:
 * - Can be tested independently
 * - Can be reused by other components
 * - No side-effects or UI coupling
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
