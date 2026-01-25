package com.niloda.pages

import androidx.compose.runtime.*
import com.niloda.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun HomePage() {
    PageLayout("Welcome to Configurable Templates") {
        Column(modifier = Modifier.gap(1.em)) {
            SpanText("A powerful template management system with DSL-based configuration.")
            
            SpanText(
                "Features:",
                modifier = Modifier
                    .fontSize(1.5.em)
                    .fontWeight(600)
                    .margin(top = 1.em, bottom = 0.5.em)
            )
            
            Column(modifier = Modifier.gap(0.5.em).padding(left = 1.em)) {
                SpanText("• Create and manage COT (Configurable Output Templates)")
                SpanText("• Type-safe parameter validation via DSL")
                SpanText("• Generate output from templates with parameters")
                SpanText("• RESTful API for integration")
                SpanText("• Web-based frontend for easy management")
            }
            
            Link(
                "/templates",
                "View COTs",
                modifier = Modifier
                    .margin(top = 2.em)
                    .fontSize(1.2.em)
            )
        }
    }
}
