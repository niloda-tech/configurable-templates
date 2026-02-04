package com.niloda.components

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    var mobileMenuOpen by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header/Navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.em)
                .backgroundColor(rgb(248, 250, 252))
                .borderBottom(1.px, LineStyle.Solid, rgb(226, 232, 240))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Desktop navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gap(2.em),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpanText(
                        "COT Editor",
                        modifier = Modifier
                            .fontSize(1.2.em)
                            .fontWeight(700)
                            .color(rgb(59, 130, 246))
                            .flex(1)
                    )
                    
                    // Desktop menu
                    Div(
                        attrs = {
                            classes("desktop-nav")
                            style {
                                property("display", "flex")
                                property("gap", "2em")
                                property("align-items", "center")
                            }
                        }
                    ) {
                        Link("/", "Home")
                        Link("/templates", "COTs")
                        Link("/about", "About")
                    }
                    
                    // Mobile menu button
                    Button(
                        attrs = {
                            onClick { mobileMenuOpen = !mobileMenuOpen }
                            style {
                                property("display", "none")
                                property("background", "transparent")
                                property("border", "none")
                                property("cursor", "pointer")
                                property("padding", "0.5em")
                                property("font-size", "1.5em")
                            }
                            classes("mobile-menu-button")
                        }
                    ) {
                        Text(if (mobileMenuOpen) "✕" else "☰")
                    }
                }
                
                // Mobile menu
                if (mobileMenuOpen) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .gap(1.em)
                            .padding(top = 1.em)
                    ) {
                        Link("/", "Home", modifier = Modifier.fontSize(1.1.em))
                        Link("/templates", "COTs", modifier = Modifier.fontSize(1.1.em))
                        Link("/about", "About", modifier = Modifier.fontSize(1.1.em))
                    }
                }
            }
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.em)
        ) {
            SpanText(
                title,
                modifier = Modifier
                    .fontSize(2.em)
                    .fontWeight(700)
                    .margin(bottom = 1.em)
            )
            content()
        }
    }
}
