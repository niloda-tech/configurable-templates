package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.dom.*

/**
 * Action buttons section - submit and cancel buttons
 */
@Composable
internal fun CotActionButtons(
    submitButtonText: String,
    isSubmitting: Boolean,
    hasValidationErrors: Boolean,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Row(modifier = Modifier.gap(1.em)) {
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
