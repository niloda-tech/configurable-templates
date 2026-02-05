package com.niloda.pages.cots

import androidx.compose.runtime.*
import com.niloda.api.ApiClient
import com.niloda.api.CotDetailResponse
import com.niloda.api.UpdateCotRequest
import com.niloda.components.CotEditor
import com.niloda.components.LoadingSpinner
import com.niloda.components.PageLayout
import com.niloda.components.ToastManager
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*

@Page("/cots/edit/{id}")
@Composable
fun EditCotPage() {
    val ctx = rememberPageContext()
    val id = ctx.route.params["id"] ?: ""
    val scope = rememberCoroutineScope()
    
    var cot by remember { mutableStateOf<CotDetailResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }
    
    // Load the COT data
    LaunchedEffect(id) {
        loading = true
        loadError = null
        ApiClient.getCot(id)
            .onSuccess { response ->
                cot = response
                loading = false
            }
            .onFailure { 
                loadError = it.message
                loading = false
            }
    }
    
    PageLayout("Edit COT") {
        when {
            loading -> {
                LoadingSpinner("Loading COT...")
            }
            loadError != null -> {
                ErrorView(loadError!!)
            }
            cot != null -> {
                Column(modifier = Modifier.gap(1.em)) {
                    // Breadcrumb
                    Link(
                        "/cots/$id",
                        "← Back to COT Details",
                        modifier = Modifier.color(rgb(59, 130, 246))
                    )
                    
                    // Description
                    SpanText(
                        "Edit ${cot!!.name}",
                        modifier = Modifier
                            .fontSize(1.3.em)
                            .fontWeight(600)
                            .color(rgb(17, 24, 39))
                    )
                    
                    // Submit Error Message
                    if (submitError != null) {
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
                                "Error Updating COT:",
                                modifier = Modifier
                                    .fontSize(1.em)
                                    .fontWeight(600)
                                    .color(rgb(220, 38, 38))
                            )
                            
                            SpanText(
                                submitError!!,
                                modifier = Modifier
                                    .fontSize(0.95.em)
                                    .color(rgb(127, 29, 29))
                            )
                        }
                    }
                    
                    // Editor Component
                    CotEditor(
                        initialName = cot!!.name,
                        initialDslCode = cot!!.dslCode,
                        submitButtonText = "Save Changes",
                        onSubmit = { name, dslCode ->
                            isSubmitting = true
                            submitError = null
                            
                            scope.launch {
                                ApiClient.updateCot(id, UpdateCotRequest(name, dslCode))
                                    .onSuccess { response ->
                                        ToastManager.showSuccess("COT updated successfully!")
                                        // Navigate back to the detail page
                                        ctx.router.navigateTo("/cots/${response.id}")
                                    }
                                    .onFailure { e ->
                                        submitError = e.message ?: "Unknown error occurred"
                                        ToastManager.showError(submitError!!)
                                        isSubmitting = false
                                    }
                            }
                        },
                        onCancel = {
                            ctx.router.navigateTo("/cots/$id")
                        },
                        isSubmitting = isSubmitting
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(modifier = Modifier.gap(1.em)) {
        SpanText(
            "Error",
            modifier = Modifier
                .fontSize(1.5.em)
                .fontWeight(600)
                .color(rgb(220, 38, 38))
        )
        SpanText(
            message,
            modifier = Modifier.color(rgb(220, 38, 38))
        )
        Link(
            "/templates",
            "← Back to Templates",
            modifier = Modifier
                .margin(top = 1.em)
                .color(rgb(59, 130, 246))
        )
    }
}
