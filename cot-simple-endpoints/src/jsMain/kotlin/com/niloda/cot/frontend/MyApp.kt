package com.niloda.cot.frontend

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.palette.button
import com.varabyte.kobweb.silk.theme.colors.palette.background
import org.jetbrains.compose.web.css.*

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    // Configure Silk theme here if needed
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            content()
        }
    }
}
