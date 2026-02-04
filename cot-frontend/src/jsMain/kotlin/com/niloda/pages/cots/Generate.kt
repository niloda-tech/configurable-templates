package com.niloda.pages.cots

import androidx.compose.runtime.*
import com.niloda.api.ApiClient
import com.niloda.api.CotDetailResponse
import com.niloda.api.GenerateRequest
import com.niloda.components.LoadingSpinner
import com.niloda.components.PageLayout
import com.niloda.components.ToastManager
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page("/cots/generate/{id}")
@Composable
fun GeneratePage() {
    val ctx = rememberPageContext()
    val id = ctx.route.params["id"] ?: ""
    val scope = rememberCoroutineScope()
    
    var cot by remember { mutableStateOf<CotDetailResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var generateError by remember { mutableStateOf<String?>(null) }
    var generatedOutput by remember { mutableStateOf<String?>(null) }
    var parameters by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var copySuccess by remember { mutableStateOf(false) }
    
    // Load the COT data
    LaunchedEffect(id) {
        loading = true
        loadError = null
        ApiClient.getCot(id)
            .onSuccess { response ->
                cot = response
                // Initialize parameters based on COT schema
                parameters = extractParametersFromDsl(response.dslCode)
                loading = false
            }
            .onFailure { 
                loadError = it.message
                loading = false
            }
    }
    
    PageLayout("Generate Output") {
        when {
            loading -> {
                LoadingSpinner("Loading template...")
            }
            loadError != null -> {
                ErrorView(loadError!!)
            }
            cot != null -> {
                Column(modifier = Modifier.gap(1.5.em)) {
                    // Breadcrumb
                    Link(
                        "/cots/$id",
                        "← Back to COT Details",
                        modifier = Modifier.color(rgb(59, 130, 246))
                    )
                    
                    // Title
                    SpanText(
                        "Generate: ${cot!!.name}",
                        modifier = Modifier
                            .fontSize(2.em)
                            .fontWeight(700)
                            .color(rgb(17, 24, 39))
                    )
                    
                    // Description
                    SpanText(
                        "Configure parameters and generate output from this template",
                        modifier = Modifier
                            .fontSize(1.em)
                            .color(rgb(107, 114, 128))
                    )
                    
                    // Generation Error Message
                    if (generateError != null) {
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
                                "⚠ Generation Error:",
                                modifier = Modifier
                                    .fontSize(1.em)
                                    .fontWeight(600)
                                    .color(rgb(220, 38, 38))
                            )
                            
                            SpanText(
                                generateError!!,
                                modifier = Modifier
                                    .fontSize(0.95.em)
                                    .color(rgb(127, 29, 29))
                            )
                        }
                    }
                    
                    // Parameter Form
                    if (parameters.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(1.5.em)
                                .backgroundColor(rgb(249, 250, 251))
                                .borderRadius(0.5.em)
                                .gap(1.em)
                        ) {
                            SpanText(
                                "Parameters",
                                modifier = Modifier
                                    .fontSize(1.3.em)
                                    .fontWeight(600)
                                    .color(rgb(17, 24, 39))
                            )
                            
                            parameters.forEach { (paramName, paramValue) ->
                                ParameterInput(
                                    name = paramName,
                                    value = paramValue,
                                    onValueChange = { newValue ->
                                        parameters = parameters + (paramName to newValue)
                                    },
                                    disabled = isGenerating
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(1.5.em)
                                .backgroundColor(rgb(249, 250, 251))
                                .borderRadius(0.5.em)
                        ) {
                            SpanText(
                                "No parameters required",
                                modifier = Modifier
                                    .fontSize(1.em)
                                    .color(rgb(107, 114, 128))
                            )
                        }
                    }
                    
                    // Generate Button
                    Button(
                        attrs = {
                            onClick {
                                if (!isGenerating) {
                                    isGenerating = true
                                    generateError = null
                                    generatedOutput = null
                                    copySuccess = false
                                    
                                    scope.launch {
                                        // Convert string parameters to JsonElement
                                        val jsonParams = parameters.mapValues { (_, value) ->
                                            convertToJsonElement(value)
                                        }
                                        
                                        ApiClient.generateOutput(id, GenerateRequest(jsonParams))
                                            .onSuccess { response ->
                                                generatedOutput = response.output
                                                ToastManager.showSuccess("Output generated successfully!")
                                                isGenerating = false
                                            }
                                            .onFailure { e ->
                                                generateError = e.message ?: "Unknown error occurred"
                                                ToastManager.showError(generateError!!)
                                                isGenerating = false
                                            }
                                    }
                                }
                            }
                            if (isGenerating) {
                                attr("disabled", "")
                            }
                            style {
                                property("padding", "0.75em 1.5em")
                                property("background-color", if (isGenerating) "rgb(156, 163, 175)" else "rgb(34, 197, 94)")
                                property("color", "white")
                                property("border", "none")
                                property("border-radius", "0.5em")
                                property("cursor", if (isGenerating) "not-allowed" else "pointer")
                                property("font-size", "1.1em")
                                property("font-weight", "600")
                                property("transition", "background-color 0.2s")
                            }
                            if (!isGenerating) {
                                onMouseOver { event ->
                                    event.currentTarget.asDynamic().style.backgroundColor = "rgb(22, 163, 74)"
                                }
                                onMouseOut { event ->
                                    event.currentTarget.asDynamic().style.backgroundColor = "rgb(34, 197, 94)"
                                }
                            }
                        }
                    ) {
                        Text(if (isGenerating) "Generating..." else "Generate Output")
                    }
                    
                    // Generated Output Display
                    if (generatedOutput != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .gap(0.5.em)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().gap(1.em),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SpanText(
                                    "Generated Output",
                                    modifier = Modifier
                                        .fontSize(1.3.em)
                                        .fontWeight(600)
                                        .color(rgb(17, 24, 39))
                                )
                                
                                // Copy button
                                Button(
                                    attrs = {
                                        onClick {
                                            generatedOutput?.let { output ->
                                                copyToClipboard(output)
                                                copySuccess = true
                                                ToastManager.showSuccess("Copied to clipboard!")
                                                // Reset copy success after 2 seconds
                                                scope.launch {
                                                    kotlinx.coroutines.delay(2000)
                                                    copySuccess = false
                                                }
                                            }
                                        }
                                        style {
                                            property("padding", "0.5em 1em")
                                            property("background-color", if (copySuccess) "rgb(34, 197, 94)" else "rgb(59, 130, 246)")
                                            property("color", "white")
                                            property("border", "none")
                                            property("border-radius", "0.375em")
                                            property("cursor", "pointer")
                                            property("font-size", "0.9em")
                                            property("font-weight", "600")
                                            property("transition", "background-color 0.2s")
                                        }
                                        onMouseOver { event ->
                                            if (!copySuccess) {
                                                event.currentTarget.asDynamic().style.backgroundColor = "rgb(37, 99, 235)"
                                            }
                                        }
                                        onMouseOut { event ->
                                            if (!copySuccess) {
                                                event.currentTarget.asDynamic().style.backgroundColor = "rgb(59, 130, 246)"
                                            }
                                        }
                                    }
                                ) {
                                    Text(if (copySuccess) "✓ Copied!" else "Copy to Clipboard")
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(1.em)
                                    .backgroundColor(rgb(31, 41, 55))
                                    .borderRadius(0.5.em)
                            ) {
                                Div(
                                    attrs = {
                                        style {
                                            property("overflow-x", "auto")
                                            property("max-height", "500px")
                                            property("overflow-y", "auto")
                                        }
                                    }
                                ) {
                                    generatedOutput?.let { output ->
                                        SpanText(
                                            output,
                                            modifier = Modifier
                                                .fontFamily("monospace")
                                                .fontSize(0.9.em)
                                                .color(rgb(229, 231, 235))
                                                .attrsModifier {
                                                    style {
                                                        property("white-space", "pre-wrap")
                                                        property("word-break", "break-word")
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterInput(
    name: String,
    value: String,
    onValueChange: (String) -> Unit,
    disabled: Boolean
) {
    Column(modifier = Modifier.gap(0.5.em)) {
        Label(forId = "param-$name") {
            SpanText(
                name,
                modifier = Modifier
                    .fontSize(1.em)
                    .fontWeight(600)
                    .color(rgb(31, 41, 55))
            )
        }
        
        // Determine input type based on parameter name and value
        val inputType = inferInputType(name, value)
        
        when (inputType) {
            ParameterInputType.BOOLEAN -> {
                // Checkbox for boolean
                Row(
                    modifier = Modifier.gap(0.5.em),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Input(
                        type = InputType.Checkbox,
                        attrs = {
                            id("param-$name")
                            checked(value.equals("true", ignoreCase = true))
                            onInput { event ->
                                val checked = event.target.asDynamic().checked as Boolean
                                onValueChange(checked.toString())
                            }
                            if (disabled) {
                                attr("disabled", "")
                            }
                            style {
                                property("width", "1.2em")
                                property("height", "1.2em")
                                property("cursor", if (disabled) "not-allowed" else "pointer")
                            }
                        }
                    )
                    SpanText(
                        "Enable",
                        modifier = Modifier
                            .fontSize(0.9.em)
                            .color(rgb(107, 114, 128))
                    )
                }
            }
            ParameterInputType.NUMBER -> {
                // Number input
                Input(
                    type = InputType.Number,
                    attrs = {
                        id("param-$name")
                        value(value)
                        onInput { event ->
                            val inputValue = event.target.asDynamic().value as? String ?: ""
                            onValueChange(inputValue)
                        }
                        placeholder("Enter number")
                        if (disabled) {
                            attr("disabled", "")
                        }
                        style {
                            property("width", "100%")
                            property("padding", "0.5em")
                            property("border", "1px solid rgb(209, 213, 219)")
                            property("border-radius", "0.375em")
                            property("font-size", "1em")
                            property("font-family", "inherit")
                        }
                    }
                )
            }
            ParameterInputType.TEXT -> {
                // Text input
                Input(
                    type = InputType.Text,
                    attrs = {
                        id("param-$name")
                        value(value)
                        onInput { onValueChange(it.value) }
                        placeholder("Enter value")
                        if (disabled) {
                            attr("disabled", "")
                        }
                        style {
                            property("width", "100%")
                            property("padding", "0.5em")
                            property("border", "1px solid rgb(209, 213, 219)")
                            property("border-radius", "0.375em")
                            property("font-size", "1em")
                            property("font-family", "inherit")
                        }
                    }
                )
            }
        }
        
        // Type hint
        SpanText(
            "Type: ${inputType.name.lowercase()}",
            modifier = Modifier
                .fontSize(0.75.em)
                .color(rgb(156, 163, 175))
        )
    }
}

private enum class ParameterInputType {
    BOOLEAN,
    NUMBER,
    TEXT
}

/**
 * Infer the input type from parameter name and current value
 */
private fun inferInputType(name: String, value: String): ParameterInputType {
    // Check if the name suggests a boolean
    val booleanNames = listOf("enabled", "flag", "show", "is", "has", "should", "can")
    if (booleanNames.any { name.contains(it, ignoreCase = true) }) {
        return ParameterInputType.BOOLEAN
    }
    
    // Check if the name suggests a number
    val numberNames = listOf("count", "num", "size", "index", "length", "amount", "quantity")
    if (numberNames.any { name.contains(it, ignoreCase = true) }) {
        return ParameterInputType.NUMBER
    }
    
    // Check if the value is a valid number
    if (value.toIntOrNull() != null) {
        return ParameterInputType.NUMBER
    }
    
    // Check if the value is a valid boolean
    if (value.equals("true", ignoreCase = true) || value.equals("false", ignoreCase = true)) {
        return ParameterInputType.BOOLEAN
    }
    
    // Default to text
    return ParameterInputType.TEXT
}

/**
 * Extract parameter names from DSL code by parsing common patterns
 */
private fun extractParametersFromDsl(dslCode: String): Map<String, String> {
    val parameters = mutableMapOf<String, String>()
    
    // Extract from conditional("paramName")
    val conditionalPattern = Regex("""conditional\s*\(\s*"([^"]+)"\s*\)""")
    conditionalPattern.findAll(dslCode).forEach { match ->
        val paramName = match.groupValues[1]
        parameters[paramName] = "false" // Default boolean value
    }
    
    // Extract from repetition("paramName")
    val repetitionPattern = Regex("""repetition\s*\(\s*"([^"]+)"\s*\)""")
    repetitionPattern.findAll(dslCode).forEach { match ->
        val paramName = match.groupValues[1]
        parameters[paramName] = "1" // Default count value
    }
    
    // Extract from oneOf("paramName")
    val oneOfPattern = Regex("""oneOf\s*\(\s*"([^"]+)"\s*\)""")
    oneOfPattern.findAll(dslCode).forEach { match ->
        val paramName = match.groupValues[1]
        parameters[paramName] = "" // Default string value
    }
    
    // Extract from ifPresent("paramName")
    val ifPresentPattern = Regex("""ifPresent\s*\(\s*"([^"]+)"\s*\)""")
    ifPresentPattern.findAll(dslCode).forEach { match ->
        val paramName = match.groupValues[1]
        parameters[paramName] = "" // Optional parameter
    }
    
    // Extract from dynamic("paramName") or Params.paramName
    val dynamicPattern = Regex("""dynamic\s*\(\s*"([^"]+)"\s*\)""")
    dynamicPattern.findAll(dslCode).forEach { match ->
        val paramName = match.groupValues[1]
        if (!parameters.containsKey(paramName)) {
            parameters[paramName] = "" // Default string value
        }
    }
    
    // Extract from Params.paramName pattern
    val paramsPattern = Regex("""Params\.(\w+)""")
    paramsPattern.findAll(dslCode).forEach { match ->
        val paramName = match.groupValues[1]
        if (!parameters.containsKey(paramName)) {
            parameters[paramName] = "" // Default string value
        }
    }
    
    return parameters
}

/**
 * Copy text to clipboard
 */
private fun copyToClipboard(text: String) {
    try {
        window.navigator.asDynamic().clipboard.writeText(text)
    } catch (e: Exception) {
        console.error("Failed to copy to clipboard", e)
    }
}

/**
 * Convert string value to JsonElement based on inferred type
 */
private fun convertToJsonElement(value: String): JsonElement {
    // Try to parse as boolean
    if (value.equals("true", ignoreCase = true)) {
        return JsonPrimitive(true)
    }
    if (value.equals("false", ignoreCase = true)) {
        return JsonPrimitive(false)
    }
    
    // Try to parse as integer
    value.toIntOrNull()?.let {
        return JsonPrimitive(it)
    }
    
    // Try to parse as double
    value.toDoubleOrNull()?.let {
        return JsonPrimitive(it)
    }
    
    // Default to string
    return JsonPrimitive(value)
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(2.em),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.gap(1.em),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                "Loading COT...",
                modifier = Modifier
                    .fontSize(1.2.em)
                    .color(rgb(107, 114, 128))
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(modifier = Modifier.gap(1.em)) {
        SpanText(
            "Error",
            modifier = Modifier
                .fontSize(1.5.em)
                .fontWeight(600)
                .color(rgb(220, 38, 38))
        )
        SpanText(
            message,
            modifier = Modifier.color(rgb(220, 38, 38))
        )
        Link(
            "/templates",
            "← Back to Templates",
            modifier = Modifier
                .margin(top = 1.em)
                .color(rgb(59, 130, 246))
        )
    }
}
