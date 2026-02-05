import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    jacoco
}

group = "com.niloda"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val arrowVersion = "2.2.0"

dependencies {
    implementation(kotlin("stdlib"))
    
    // cot-dsl module dependency
    implementation(project(":cot-dsl"))

    // Arrow for typed error handling
    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.13")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.13")

    // Tests
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

// Configure JaCoCo for test coverage
jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
