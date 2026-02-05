import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    kotlin("multiplatform") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("plugin.compose") version "2.2.20"
    id("org.jetbrains.compose") version "1.8.0"
    id("com.varabyte.kobweb.application") version "0.23.3"
}

group = "com.niloda"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("COT Editor - Configurable Templates Editor")
        }
    }
}

kotlin {
    configAsKobwebApplication("cot-frontend")
    
    sourceSets {
        jsMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.html.core)
            implementation("com.varabyte.kobweb:kobweb-core:0.23.3")
            implementation("com.varabyte.kobweb:kobweb-silk:0.23.3")
            // Removed unused silk-icons-fa dependency for smaller bundle size
            
            // Ktor client for API calls to backend
            implementation("io.ktor:ktor-client-core:3.0.1")
            implementation("io.ktor:ktor-client-js:3.0.1")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.1")
            
            // Kotlinx serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
    }
}
