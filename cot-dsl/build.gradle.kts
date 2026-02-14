import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21"
    id("application")
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.niloda"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    val arrowVersion = "2.2.0"
    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    val main by getting {
        java.setSrcDirs(listOf("../src/main/kotlin"))
        resources.setSrcDirs(listOf("../src/main/resources"))
    }
    val test by getting {
        java.setSrcDirs(listOf("../src/test/kotlin"))
        resources.setSrcDirs(listOf("../src/test/resources"))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

    application {
        mainClass.set("com.niloda.MainKt")
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier.set("all")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass.get()))
        }
    }