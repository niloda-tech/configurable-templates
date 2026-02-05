package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

/**
 * Name input section with label and text field
 */
@Composable
internal fun CotNameSection(
    name: String,
    isSubmitting: Boolean,
    onNameChange: (String) -> Unit
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
