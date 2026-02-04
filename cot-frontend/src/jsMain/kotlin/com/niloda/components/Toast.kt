package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class ToastType {
    SUCCESS, ERROR, INFO, WARNING
}

data class ToastMessage(
    val id: String = kotlin.random.Random.nextInt().toString(),
    val message: String,
    val type: ToastType = ToastType.INFO,
    val duration: Long = 5000L
)

/**
 * Global toast manager using composition local
 */
object ToastManager {
    private val _toasts = mutableStateListOf<ToastMessage>()
    val toasts: List<ToastMessage> get() = _toasts
    
    fun showToast(message: String, type: ToastType = ToastType.INFO, duration: Long = 5000L) {
        val toast = ToastMessage(
            message = message,
            type = type,
            duration = duration
        )
        _toasts.add(toast)
    }
    
    fun showSuccess(message: String, duration: Long = 3000L) {
        showToast(message, ToastType.SUCCESS, duration)
    }
    
    fun showError(message: String, duration: Long = 5000L) {
        showToast(message, ToastType.ERROR, duration)
    }
    
    fun showInfo(message: String, duration: Long = 3000L) {
        showToast(message, ToastType.INFO, duration)
    }
    
    fun showWarning(message: String, duration: Long = 4000L) {
        showToast(message, ToastType.WARNING, duration)
    }
    
    fun removeToast(id: String) {
        _toasts.removeAll { it.id == id }
    }
}

/**
 * Toast container - should be placed at the root of the app
 */
@Composable
fun ToastContainer() {
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .position(Position.Fixed)
            .top(1.em)
            .right(1.em)
            .zIndex(9999)
    ) {
        Div(
            attrs = {
                style {
                    property("width", "min(90vw, 400px)")
                }
            }
        ) {
            Column(modifier = Modifier.gap(0.75.em)) {
                ToastManager.toasts.forEach { toast ->
                    LaunchedEffect(toast.id) {
                        scope.launch {
                            delay(toast.duration)
                            ToastManager.removeToast(toast.id)
                        }
                    }
                    
                    Toast(toast) {
                        ToastManager.removeToast(toast.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun Toast(toast: ToastMessage, onDismiss: () -> Unit) {
    val (bgColor, borderColor, textColor, icon) = when (toast.type) {
        ToastType.SUCCESS -> listOf(
            "rgb(240, 253, 244)",
            "rgb(34, 197, 94)",
            "rgb(22, 101, 52)",
            "✓"
        )
        ToastType.ERROR -> listOf(
            "rgb(254, 242, 242)",
            "rgb(239, 68, 68)",
            "rgb(127, 29, 29)",
            "✕"
        )
        ToastType.WARNING -> listOf(
            "rgb(254, 252, 232)",
            "rgb(234, 179, 8)",
            "rgb(113, 63, 18)",
            "⚠"
        )
        ToastType.INFO -> listOf(
            "rgb(239, 246, 255)",
            "rgb(59, 130, 246)",
            "rgb(30, 58, 138)",
            "ℹ"
        )
    }
    
    Div(
        attrs = {
            style {
                property("display", "flex")
                property("align-items", "flex-start")
                property("gap", "0.75em")
                property("padding", "1em")
                property("background-color", bgColor)
                property("border-left", "4px solid $borderColor")
                property("border-radius", "0.375em")
                property("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)")
                property("animation", "slideInRight 0.3s ease-out")
                property("min-width", "300px")
            }
        }
    ) {
        // Icon
        SpanText(
            icon,
            modifier = Modifier
                .fontSize(1.25.em)
                .color(parseRgbColor(borderColor))
                .fontWeight(700)
        )
        
        // Message
        SpanText(
            toast.message,
            modifier = Modifier
                .flex(1)
                .fontSize(0.95.em)
                .color(parseRgbColor(textColor))
        )
        
        // Close button
        Button(
            attrs = {
                onClick { onDismiss() }
                style {
                    property("background", "transparent")
                    property("border", "none")
                    property("cursor", "pointer")
                    property("padding", "0")
                    property("color", textColor)
                    property("font-size", "1.2em")
                    property("line-height", "1")
                    property("opacity", "0.6")
                    property("transition", "opacity 0.2s")
                }
                onMouseOver { event ->
                    event.currentTarget.asDynamic().style.opacity = "1"
                }
                onMouseOut { event ->
                    event.currentTarget.asDynamic().style.opacity = "0.6"
                }
            }
        ) {
            Text("×")
        }
    }
}

/**
 * Inject CSS animations for toast
 */
fun injectToastStyles() {
    val style = kotlinx.browser.document.createElement("style")
    style.textContent = """
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    """.trimIndent()
    kotlinx.browser.document.head?.appendChild(style)
}

/**
 * Parse RGB color string like "rgb(255, 0, 0)" to CSSColorValue
 */
private fun parseRgbColor(rgbString: String): CSSColorValue {
    val values = rgbString.removePrefix("rgb(").removeSuffix(")").split(",").map { it.trim().toInt() }
    return rgb(values[0], values[1], values[2])
}
