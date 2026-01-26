package com.niloda.pages.cots

import androidx.compose.runtime.*
import com.niloda.api.ApiClient
import com.niloda.api.CotDetailResponse
import com.niloda.components.PageLayout
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Page("{id}")
@Composable
fun CotDetailPage() {
    val ctx = rememberPageContext()
    val id = ctx.route.params["id"] ?: ""
    val scope = rememberCoroutineScope()
    
    var cot by remember { mutableStateOf<CotDetailResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }
    
    LaunchedEffect(id) {
        loading = true
        error = null
        ApiClient.getCot(id)
            .onSuccess { response ->
                cot = response
                loading = false
            }
            .onFailure { 
                error = it.message
                loading = false
            }
    }
    
    PageLayout("COT Details") {
        when {
            loading -> {
                LoadingSpinner()
            }
            error != null -> {
                ErrorMessage(error!!)
            }
            cot != null -> {
                CotDetailView(cot!!, showDeleteConfirm, deleting, 
                    onDeleteClick = { showDeleteConfirm = true },
                    onDeleteConfirm = {
                        deleting = true
                        scope.launch {
                            ApiClient.deleteCot(id)
                                .onSuccess {
                                    // Navigate back to templates list
                                    ctx.router.navigateTo("/templates")
                                }
                                .onFailure {
                                    error = "Failed to delete: ${it.message}"
                                    deleting = false
                                    showDeleteConfirm = false
                                }
                        }
                    },
                    onDeleteCancel = { showDeleteConfirm = false }
                )
            }
        }
    }
}

@Composable
private fun LoadingSpinner() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(2.em),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.gap(1.em),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                "Loading...",
                modifier = Modifier
                    .fontSize(1.2.em)
                    .color(rgb(107, 114, 128))
            )
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
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

@Composable
private fun CotDetailView(
    cot: CotDetailResponse,
    showDeleteConfirm: Boolean,
    deleting: Boolean,
    onDeleteClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit
) {
    Column(modifier = Modifier.gap(1.5.em)) {
        // Header with back link and actions
        Row(
            modifier = Modifier.fillMaxWidth().gap(1.em),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Link(
                "/templates",
                "← Back to Templates",
                modifier = Modifier.color(rgb(59, 130, 246))
            )
        }
        
        // COT Name
        SpanText(
            cot.name,
            modifier = Modifier
                .fontSize(2.em)
                .fontWeight(700)
                .color(rgb(17, 24, 39))
        )
        
        // Metadata
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.em)
                .backgroundColor(rgb(249, 250, 251))
                .borderRadius(0.5.em)
                .gap(0.5.em)
        ) {
            SpanText(
                "Metadata",
                modifier = Modifier
                    .fontSize(1.1.em)
                    .fontWeight(600)
                    .margin(bottom = 0.5.em)
            )
            
            MetadataRow("ID", cot.id)
            MetadataRow("Created", cot.createdAt)
            MetadataRow("Updated", cot.updatedAt)
        }
        
        // DSL Code
        Column(modifier = Modifier.gap(0.5.em)) {
            SpanText(
                "DSL Code",
                modifier = Modifier
                    .fontSize(1.3.em)
                    .fontWeight(600)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.em)
                    .backgroundColor(rgb(31, 41, 55))
                    .borderRadius(0.5.em)
            ) {
                Div(
                    attrs = {
                        style {
                            property("overflow-x", "auto")
                        }
                    }
                ) {
                    SpanText(
                        cot.dslCode,
                        modifier = Modifier
                            .fontFamily("monospace")
                            .fontSize(0.9.em)
                            .color(rgb(229, 231, 235))
                            .attrsModifier {
                                style {
                                    property("white-space", "pre")
                                }
                            }
                    )
                }
            }
        }
        
        // Delete Button
        if (!showDeleteConfirm) {
            Button(
                attrs = {
                    onClick { onDeleteClick() }
                    style {
                        property("padding", "0.75em 1.5em")
                        property("background-color", "rgb(220, 38, 38)")
                        property("color", "white")
                        property("border", "none")
                        property("border-radius", "0.5em")
                        property("cursor", "pointer")
                        property("font-size", "1em")
                        property("font-weight", "600")
                        property("transition", "background-color 0.2s")
                    }
                    onMouseOver { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(185, 28, 28)"
                    }
                    onMouseOut { event ->
                        event.currentTarget.asDynamic().style.backgroundColor = "rgb(220, 38, 38)"
                    }
                }
            ) {
                Text("Delete COT")
            }
        } else {
            DeleteConfirmation(
                deleting = deleting,
                onConfirm = onDeleteConfirm,
                onCancel = onDeleteCancel
            )
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Div(
        attrs = {
            style {
                property("display", "flex")
                property("gap", "1em")
            }
        }
    ) {
        SpanText(
            "$label:",
            modifier = Modifier
                .fontWeight(600)
                .color(rgb(75, 85, 99))
        )
        SpanText(
            value,
            modifier = Modifier
                .color(rgb(107, 114, 128))
                .fontFamily(if (label == "ID") "monospace" else "")
        )
    }
}

@Composable
private fun DeleteConfirmation(
    deleting: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.5.em)
            .backgroundColor(rgb(254, 242, 242))
            .border(1.px, LineStyle.Solid, rgb(220, 38, 38))
            .borderRadius(0.5.em)
            .gap(1.em)
    ) {
        SpanText(
            "Confirm Deletion",
            modifier = Modifier
                .fontSize(1.2.em)
                .fontWeight(600)
                .color(rgb(220, 38, 38))
        )
        
        SpanText(
            "Are you sure you want to delete this COT? This action cannot be undone.",
            modifier = Modifier.color(rgb(127, 29, 29))
        )
        
        Row(modifier = Modifier.gap(1.em)) {
            Button(
                attrs = {
                    if (deleting) {
                        attr("disabled", "")
                    }
                    onClick { if (!deleting) onConfirm() }
                    style {
                        property("padding", "0.5em 1em")
                        property("background-color", if (deleting) "rgb(156, 163, 175)" else "rgb(220, 38, 38)")
                        property("color", "white")
                        property("border", "none")
                        property("border-radius", "0.375em")
                        property("cursor", if (deleting) "not-allowed" else "pointer")
                        property("font-weight", "600")
                    }
                }
            ) {
                Text(if (deleting) "Deleting..." else "Yes, Delete")
            }
            
            Button(
                attrs = {
                    if (deleting) {
                        attr("disabled", "")
                    }
                    onClick { if (!deleting) onCancel() }
                    style {
                        property("padding", "0.5em 1em")
                        property("background-color", "rgb(243, 244, 246)")
                        property("color", "rgb(17, 24, 39)")
                        property("border", "1px solid rgb(209, 213, 219)")
                        property("border-radius", "0.375em")
                        property("cursor", if (deleting) "not-allowed" else "pointer")
                        property("font-weight", "600")
                    }
                }
            ) {
                Text("Cancel")
            }
        }
    }
}
