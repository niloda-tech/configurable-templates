import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    kotlin("multiplatform") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("plugin.compose") version "2.2.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("com.varabyte.kobweb.library") version "0.23.3"
}

group = "com.niloda"
version = "1.0-SNAPSHOT"

val ktorVersion = "3.0.1"
val arrowVersion = "2.2.0"

kotlin {
    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                    freeCompilerArgs.add("-Xcontext-parameters")
                }
            }
        }
    }
    
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "cot-frontend.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                // cot-dsl module dependency
                implementation(project(":cot-dsl"))

                // Ktor server
                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
                
                // CORS support for frontend
                implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
                
                // Arrow for typed error handling  
                implementation("io.arrow-kt:arrow-core:$arrowVersion")
                implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

                // Logging (SLF4J Simple for quick run)
                runtimeOnly("org.slf4j:slf4j-simple:2.0.13")
            }
        }
        
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
                implementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.html.core)
                implementation("com.varabyte.kobweb:kobweb-core:0.23.3")
                implementation("com.varabyte.kobweb:kobweb-silk:0.23.3")
                implementation("com.varabyte.kobwebx:silk-icons-fa:0.23.3")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
