package com.niloda.pages

import androidx.compose.runtime.*
import com.niloda.cot.frontend.api.ApiClient
import com.niloda.cot.frontend.api.CotSummary
import com.niloda.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun TemplatesPage() {
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
        when {
            loading -> {
                SpanText("Loading COTs...")
            }
            error != null -> {
                SpanText(
                    "Error: $error",
                    modifier = Modifier.color(rgb(220, 38, 38))
                )
            }
            cots.isEmpty() -> {
                SpanText("No COTs found. Create your first COT using the API!")
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

@Composable
private fun CotCard(cot: CotSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.em)
            .border(1.px, LineStyle.Solid, rgb(209, 213, 219))
            .borderRadius(0.5.em)
            .gap(0.5.em)
    ) {
        SpanText(
            cot.name,
            modifier = Modifier
                .fontSize(1.3.em)
                .fontWeight(600)
        )
        
        Row(modifier = Modifier.gap(1.em)) {
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
