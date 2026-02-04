package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

/**
 * A reusable loading spinner component
 * Shows a spinning animation with optional text
 */
@Composable
fun LoadingSpinner(
    text: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Div(
            attrs = {
                style {
                    property("display", "flex")
                    property("flex-direction", "column")
                    property("align-items", "center")
                    property("gap", "1em")
                    property("padding", "2em")
                }
            }
        ) {
            // Spinner
            Div(
                attrs = {
                    style {
                        property("width", "48px")
                        property("height", "48px")
                        property("border", "4px solid rgb(229, 231, 235)")
                        property("border-top-color", "rgb(59, 130, 246)")
                        property("border-radius", "50%")
                        property("animation", "spin 0.8s linear infinite")
                    }
                }
            )
            
            // Loading text
            SpanText(
                text,
                modifier = Modifier
                    .fontSize(1.em)
                    .color(rgb(107, 114, 128))
            )
        }
    }
}

/**
 * Inject CSS keyframes for spinner animation into the page
 * Call this once in the app initialization
 */
fun injectSpinnerStyles() {
    val style = kotlinx.browser.document.createElement("style")
    style.textContent = """
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    """.trimIndent()
    kotlinx.browser.document.head?.appendChild(style)
}
