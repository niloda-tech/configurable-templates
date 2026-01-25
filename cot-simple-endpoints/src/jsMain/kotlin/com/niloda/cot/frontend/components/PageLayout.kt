package com.niloda.cot.frontend.components

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
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.css.*

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header/Navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.em)
                .backgroundColor(colorMode.toPalette().background)
                .borderBottom(1.px, LineStyle.Solid, colorMode.toPalette().color)
        ) {
            Row(
                modifier = Modifier.gap(2.em),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Link("/", "Home")
                Link("/templates", "COTs")
                Link("/about", "About")
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
                    .fontWeight(FontWeight.Bold)
                    .margin(bottom = 1.em)
            )
            content()
        }
    }
}
