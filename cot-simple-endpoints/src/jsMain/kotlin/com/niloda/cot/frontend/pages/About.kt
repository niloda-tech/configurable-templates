package com.niloda.cot.frontend.pages

import androidx.compose.runtime.*
import com.niloda.cot.frontend.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun AboutPage() {
    PageLayout("About") {
        Column(modifier = Modifier.gap(1.em)) {
            SpanText("Configurable Templates is a template management system built with:")
            
            Column(modifier = Modifier.gap(0.5.em).padding(left = 1.em)) {
                SpanText("• Kotlin 2.2.21")
                SpanText("• Ktor 3.0.1 (Backend)")
                SpanText("• Kobweb 0.23.3 (Frontend)")
                SpanText("• Arrow for functional error handling")
            }
            
            SpanText(
                "Backend API is running on port 8080",
                modifier = Modifier.margin(top = 1.em)
            )
            
            SpanText(
                "Frontend is running on port 8081",
                modifier = Modifier
            )
        }
    }
}
