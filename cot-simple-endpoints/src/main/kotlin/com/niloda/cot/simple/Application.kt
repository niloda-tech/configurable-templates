package com.niloda.cot.simple

import com.niloda.cot.simple.api.cotRoutes
import com.niloda.cot.simple.repository.InMemoryCotRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Initialize repository
    val repository = InMemoryCotRepository()
    
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/health") {
            call.respond(HealthResponse("ok"))
        }

        get("/") {
            call.respond(mapOf("name" to "cot-simple-endpoints", "message" to "Welcome"))
        }
        
        // COT CRUD routes
        cotRoutes(repository)
    }
}

@Serializable
data class HealthResponse(val status: String)
