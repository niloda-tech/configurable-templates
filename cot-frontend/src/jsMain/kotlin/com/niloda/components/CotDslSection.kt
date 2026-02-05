package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

/**
 * DSL code editor section with label, textarea, and tips
 */
@Composable
internal fun CotDslSection(
    dslCode: String,
    isSubmitting: Boolean,
    onDslCodeChange: (String) -> Unit
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
