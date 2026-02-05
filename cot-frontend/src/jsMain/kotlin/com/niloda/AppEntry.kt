package com.niloda

import androidx.compose.runtime.*
import com.niloda.components.ToastContainer
import com.niloda.components.injectSpinnerStyles
import com.niloda.components.injectToastStyles
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*

@InitSilk
fun initStyles(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        registerStyleBase("body") { Modifier.scrollBehavior(ScrollBehavior.Smooth) }
    }
    
    // Inject CSS animations for toast and spinner
    injectToastStyles()
    injectSpinnerStyles()
    injectResponsiveStyles()
}

/**
 * Inject responsive CSS styles for mobile support
 */
fun injectResponsiveStyles() {
    val style = kotlinx.browser.document.createElement("style")
    style.textContent = """
        /* Desktop navigation visible by default */
        .desktop-nav {
            display: flex !important;
        }
        
        /* Mobile menu button hidden by default */
        .mobile-menu-button {
            display: none !important;
        }
        
        /* Mobile responsive styles */
        @media (max-width: 768px) {
            /* Hide desktop nav on mobile */
            .desktop-nav {
                display: none !important;
            }
            
            /* Show mobile menu button */
            .mobile-menu-button {
                display: block !important;
            }
            
            /* Reduce padding on mobile */
            body {
                padding: 1em;
            }
            
            /* Make primary action buttons full-width on mobile */
            button[type="submit"],
            button.primary-action {
                width: 100%;
            }
        }
        
        /* Tablet adjustments */
        @media (max-width: 1024px) and (min-width: 769px) {
            body {
                padding: 1.5em;
            }
        }
    """.trimIndent()
    kotlinx.browser.document.head?.appendChild(style)
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            content()
            ToastContainer()  // Global toast notifications
        }
    }
}
