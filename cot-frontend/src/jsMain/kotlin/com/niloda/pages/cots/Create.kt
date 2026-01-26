package com.niloda.pages.cots

import androidx.compose.runtime.*
import com.niloda.api.ApiClient
import com.niloda.api.CreateCotRequest
import com.niloda.components.CotEditor
import com.niloda.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*

@Page("/cots/create")
@Composable
fun CreateCotPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()
    
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    PageLayout("Create New COT") {
        Column(modifier = Modifier.gap(1.em)) {
            // Breadcrumb
            Link(
                "/templates",
                "← Back to Templates",
                modifier = Modifier.color(rgb(59, 130, 246))
            )
            
            // Description
            SpanText(
                "Create a new Configurable Output Template using Kotlin DSL",
                modifier = Modifier
                    .color(rgb(107, 114, 128))
                    .margin(bottom = 1.em)
            )
            
            // Error Message
            if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.em)
                        .backgroundColor(rgb(254, 242, 242))
                        .border(1.px, LineStyle.Solid, rgb(220, 38, 38))
                        .borderRadius(0.375.em)
                        .gap(0.5.em)
                ) {
                    SpanText(
                        "Error Creating COT:",
                        modifier = Modifier
                            .fontSize(1.em)
                            .fontWeight(600)
                            .color(rgb(220, 38, 38))
                    )
                    
                    SpanText(
                        error!!,
                        modifier = Modifier
                            .fontSize(0.95.em)
                            .color(rgb(127, 29, 29))
                    )
                }
            }
            
            // Editor Component
            CotEditor(
                initialName = "",
                initialDslCode = """cot("MyTemplate") {
    "Hello, ".text
    Params.name ifTrueThen "World"
    "!".text
}""",
                submitButtonText = "Create COT",
                onSubmit = { name, dslCode ->
                    isSubmitting = true
                    error = null
                    
                    scope.launch {
                        ApiClient.createCot(CreateCotRequest(name, dslCode))
                            .onSuccess { response ->
                                // Navigate to the newly created COT's detail page
                                ctx.router.navigateTo("/cots/${response.id}")
                            }
                            .onFailure { e ->
                                error = e.message ?: "Unknown error occurred"
                                isSubmitting = false
                            }
                    }
                },
                onCancel = {
                    ctx.router.navigateTo("/templates")
                },
                isSubmitting = isSubmitting
            )
        }
    }
}
