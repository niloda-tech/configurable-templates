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
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .gap(1.5.em)
    ) {
        // Name Input
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
                    onInput { name = it.value }
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
        
        // DSL Code Editor
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
                    onInput { dslCode = it.value }
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
        }
        
        // Validation Errors
        if (validationErrors.isNotEmpty()) {
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
                
                validationErrors.forEach { error ->
                    SpanText(
                        "• $error",
                        modifier = Modifier
                            .fontSize(0.95.em)
                            .color(rgb(127, 29, 29))
                    )
                }
            }
        }
        
        // Action Buttons
        Row(modifier = Modifier.gap(1.em)) {
            Button(
                attrs = {
                    onClick {
                        if (!isSubmitting && validationErrors.isEmpty()) {
                            onSubmit(name, dslCode)
                        }
                    }
                    if (isSubmitting || validationErrors.isNotEmpty()) {
                        attr("disabled", "")
                    }
                    style {
                        property("padding", "0.75em 1.5em")
                        property("background-color", 
                            if (isSubmitting || validationErrors.isNotEmpty()) 
                                "rgb(156, 163, 175)" 
                            else 
                                "rgb(59, 130, 246)"
                        )
                        property("color", "white")
                        property("border", "none")
                        property("border-radius", "0.375em")
                        property("cursor", 
                            if (isSubmitting || validationErrors.isNotEmpty()) 
                                "not-allowed" 
                            else 
                                "pointer"
                        )
                        property("font-size", "1em")
                        property("font-weight", "600")
                        property("transition", "background-color 0.2s")
                    }
                    if (!isSubmitting && validationErrors.isEmpty()) {
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
