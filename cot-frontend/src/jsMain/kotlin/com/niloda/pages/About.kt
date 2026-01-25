package com.niloda.pages

import androidx.compose.runtime.*
import com.niloda.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun AboutPage() {
    PageLayout("About COT Editor") {
        Column(modifier = Modifier.gap(1.em)) {
            SpanText("COT Editor is a template management system built with:")
            
            Column(modifier = Modifier.gap(0.5.em).padding(left = 1.em)) {
                SpanText("• Kotlin 2.2.20")
                SpanText("• Ktor 3.0.1 (Backend)")
                SpanText("• Kobweb 0.23.3 (Frontend)")
                SpanText("• Arrow for functional error handling")
            }
            
            SpanText(
                "Architecture:",
                modifier = Modifier
                    .fontSize(1.5.em)
                    .fontWeight(600)
                    .margin(top = 1.em, bottom = 0.5.em)
            )
            
            Column(modifier = Modifier.gap(0.5.em).padding(left = 1.em)) {
                SpanText("• Backend: Ktor server on port 8080 (separate module)")
                SpanText("• Frontend: Kobweb on port 8081 (separate module)")
                SpanText("• Modular architecture to avoid dependency conflicts")
            }
        }
    }
}
