plugins {
    kotlin("multiplatform") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("plugin.compose") version "2.2.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("com.varabyte.kobweb.library") version "0.23.3"
}

group = "com.niloda"
version = "1.0-SNAPSHOT"

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "cot-frontend.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.html.core)
                implementation("com.varabyte.kobweb:kobweb-core:0.23.3")
                implementation("com.varabyte.kobweb:kobweb-silk:0.23.3")
                implementation("com.varabyte.kobwebx:silk-icons-fa:0.23.3")
                
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
}
