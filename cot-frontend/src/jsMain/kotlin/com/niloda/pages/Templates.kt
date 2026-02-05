package com.niloda.pages

import androidx.compose.runtime.*
import com.niloda.api.ApiClient
import com.niloda.api.model.CotSummary
import com.niloda.components.LoadingSpinner
import com.niloda.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.compose.ui.attrsModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import com.varabyte.kobweb.core.rememberPageContext

@Page
@Composable
fun TemplatesPage() {
    val ctx = rememberPageContext()
    var cots by remember { mutableStateOf<List<CotSummary>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        loading = true
        ApiClient.listCots()
            .onSuccess { response ->
                cots = response.cots
                loading = false
            }
            .onFailure { 
                error = it.message
                loading = false
            }
    }
    
    PageLayout("COT Templates") {
        Column(modifier = Modifier.gap(1.5.em)) {
            // Create button
            Button(
                attrs = {
                    onClick { ctx.router.navigateTo("/cots/create") }
                    style {
                        property("padding", "0.75em 1.5em")
                        property("background-color", "rgb(59, 130, 246)")
                        property("color", "white")
                        property("border", "none")
                        property("border-radius", "0.375em")
                        property("cursor", "pointer")
                        property("font-size", "1em")
                        property("font-weight", "600")
                        property("transition", "background-color 0.2s")
                        property("align-self", "flex-start")
                    }
                    onMouseOver { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(37, 99, 235)"
                    }
                    onMouseOut { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(59, 130, 246)"
                    }
                }
            ) {
                Text("+ Create New COT")
            }
            
            when {
                loading -> {
                    LoadingSpinner("Loading COTs...")
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.em)
                            .backgroundColor(rgb(254, 242, 242))
                            .border(1.px, LineStyle.Solid, rgb(220, 38, 38))
                            .borderRadius(0.375.em)
                    ) {
                        SpanText(
                            "Error loading COTs: $error",
                            modifier = Modifier.color(rgb(220, 38, 38))
                        )
                    }
                }
                cots.isEmpty() -> {
                    SpanText("No COTs found. Create your first COT!")
                }
                else -> {
                    Column(modifier = Modifier.gap(1.em)) {
                        cots.forEach { cot ->
                            CotCard(cot)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CotCard(cot: CotSummary) {
    Link(
        path = "/cots/${cot.id}"
    ) {
        Div(
            attrs = {
                classes("cot-card")
                style {
                    property("width", "100%")
                    property("padding", "1em")
                    property("border", "1px solid rgb(209, 213, 219)")
                    property("border-radius", "0.5em")
                    property("background-color", "rgb(255, 255, 255)")
                    property("transition", "all 0.2s ease-in-out")
                    property("cursor", "pointer")
                    property("text-decoration", "none")
                    property("display", "flex")
                    property("flex-direction", "column")
                    property("gap", "0.5em")
                }
                onMouseOver { event ->
                    val target = event.currentTarget.asDynamic()
                    target.style.borderColor = "rgb(59, 130, 246)"
                    target.style.backgroundColor = "rgb(249, 250, 251)"
                }
                onMouseOut { event ->
                    val target = event.currentTarget.asDynamic()
                    target.style.borderColor = "rgb(209, 213, 219)"
                    target.style.backgroundColor = "rgb(255, 255, 255)"
                }
            }
        ) {
            SpanText(
                cot.name,
                modifier = Modifier
                    .fontSize(1.3.em)
                    .fontWeight(600)
                    .color(rgb(17, 24, 39))
            )

            Div(
                attrs = {
                    style {
                        property("display", "flex")
                        property("gap", "1em")
                    }
                }
            ) {
                SpanText(
                    "Created: ${cot.createdAt}",
                    modifier = Modifier.color(rgb(107, 114, 128)).fontSize(0.9.em)
                )
                SpanText(
                    "Updated: ${cot.updatedAt}",
                    modifier = Modifier.color(rgb(107, 114, 128)).fontSize(0.9.em)
                )
            }

            SpanText(
                "ID: ${cot.id}",
                modifier = Modifier
                    .color(rgb(107, 114, 128))
                    .fontSize(0.85.em)
                    .fontFamily("monospace")
            )
        }
    }
}
