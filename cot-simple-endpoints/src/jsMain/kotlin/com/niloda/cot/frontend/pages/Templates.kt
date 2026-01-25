package com.niloda.cot.frontend.pages

import androidx.compose.runtime.*
import com.niloda.cot.frontend.api.ApiClient
import com.niloda.cot.frontend.api.TemplateResponse
import com.niloda.cot.frontend.components.PageLayout
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
    var templates by remember { mutableStateOf<List<TemplateResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        loading = true
        ApiClient.listTemplates()
            .onSuccess { 
                templates = it
                loading = false
            }
            .onFailure { 
                error = it.message
                loading = false
            }
    }
    
    PageLayout("Templates") {
        when {
            loading -> {
                SpanText("Loading templates...")
            }
            error != null -> {
                SpanText(
                    "Error: $error",
                    modifier = Modifier.color(rgb(220, 38, 38))
                )
            }
            templates.isEmpty() -> {
                SpanText("No templates found. Create your first template using the API!")
            }
            else -> {
                Column(modifier = Modifier.gap(1.em)) {
                    templates.forEach { template ->
                        TemplateCard(template)
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(template: TemplateResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.em)
            .border(1.px, LineStyle.Solid, rgb(209, 213, 219))
            .borderRadius(0.5.em)
            .gap(0.5.em)
    ) {
        SpanText(
            template.name,
            modifier = Modifier
                .fontSize(1.3.em)
                .fontWeight(FontWeight.Bold)
        )
        
        template.description?.let { desc ->
            SpanText(desc, modifier = Modifier.color(rgb(107, 114, 128)))
        }
        
        if (template.parameters.isNotEmpty()) {
            SpanText(
                "Parameters:",
                modifier = Modifier
                    .fontSize(0.9.em)
                    .fontWeight(FontWeight.SemiBold)
                    .margin(top = 0.5.em)
            )
            Column(modifier = Modifier.gap(0.25.em).padding(left = 1.em)) {
                template.parameters.forEach { param ->
                    Row(modifier = Modifier.gap(0.5.em)) {
                        SpanText(
                            param.name,
                            modifier = Modifier.fontWeight(FontWeight.Medium)
                        )
                        SpanText(
                            "(${param.type})",
                            modifier = Modifier.color(rgb(107, 114, 128))
                        )
                        if (param.required) {
                            SpanText(
                                "*",
                                modifier = Modifier.color(rgb(220, 38, 38))
                            )
                        }
                    }
                }
            }
        }
    }
}
