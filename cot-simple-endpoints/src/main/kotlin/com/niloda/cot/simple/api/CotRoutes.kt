package com.niloda.cot.simple.api

import arrow.core.raise.either
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.template.DomainError
import com.niloda.cot.simple.api.models.*
import com.niloda.cot.simple.repository.CotRepository
import com.niloda.cot.simple.repository.StoredCot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.format.DateTimeFormatter

/**
 * Configure COT CRUD routes
 */
fun Route.cotRoutes(repository: CotRepository) {
    route("/api/cots") {
        // List all COTs
        get {
            repository.list().fold(
                ifLeft = { error ->
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("InternalError", error.toString())
                    )
                },
                ifRight = { cots ->
                    call.respond(
                        HttpStatusCode.OK,
                        CotListResponse(cots.map { it.toSummary() })
                    )
                }
            )
        }
        
        // Get single COT
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("InvalidRequest", "Missing id parameter")
            )
            
            repository.findById(id).fold(
                ifLeft = { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("CotNotFound", error.toString())
                    )
                },
                ifRight = { cot ->
                    call.respond(HttpStatusCode.OK, cot.toDetailResponse())
                }
            )
        }
        
        // Create COT
        post {
            val request = call.receive<CreateCotRequest>()
            
            // Create a simple COT using the DSL
            // For Phase 1, we create a basic static template
            // Future phases will implement full DSL code parsing/evaluation
            val cotResult = cot(request.name) {
                "Template: ${request.name}\n".text
                "DSL Code:\n${request.dslCode}\n".text
            }
            
            cotResult.fold(
                ifLeft = { error ->
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("InvalidDsl", error.toString())
                    )
                },
                ifRight = { cot ->
                    repository.create(cot, request.dslCode).fold(
                        ifLeft = { error ->
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse("CreateError", error.toString())
                            )
                        },
                        ifRight = { stored ->
                            call.respond(HttpStatusCode.Created, stored.toDetailResponse())
                        }
                    )
                }
            )
        }
        
        // Update COT
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("InvalidRequest", "Missing id parameter")
            )
            val request = call.receive<UpdateCotRequest>()
            
            // Create a simple COT using the DSL
            // For Phase 1, we create a basic static template
            // Future phases will implement full DSL code parsing/evaluation
            val cotResult = cot(request.name) {
                "Template: ${request.name}\n".text
                "DSL Code:\n${request.dslCode}\n".text
            }
            
            cotResult.fold(
                ifLeft = { error ->
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("InvalidDsl", error.toString())
                    )
                },
                ifRight = { cot ->
                    repository.update(id, cot, request.dslCode).fold(
                        ifLeft = { error ->
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse("CotNotFound", error.toString())
                            )
                        },
                        ifRight = { stored ->
                            call.respond(HttpStatusCode.OK, stored.toDetailResponse())
                        }
                    )
                }
            )
        }
        
        // Delete COT
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("InvalidRequest", "Missing id parameter")
            )
            
            repository.delete(id).fold(
                ifLeft = { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("CotNotFound", error.toString())
                    )
                },
                ifRight = {
                    call.respond(HttpStatusCode.NoContent)
                }
            )
        }
    }
}

/**
 * Convert StoredCot to CotSummary
 */
private fun StoredCot.toSummary(): CotSummary {
    return CotSummary(
        id = id,
        name = cot.name,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

/**
 * Convert StoredCot to CotDetailResponse
 */
private fun StoredCot.toDetailResponse(): CotDetailResponse {
    return CotDetailResponse(
        id = id,
        name = cot.name,
        dslCode = dslCode,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}
