package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*

/**
 * Validation errors panel - displays list of validation errors
 */
@Composable
internal fun CotValidationErrorsPanel(errors: List<String>) {
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
