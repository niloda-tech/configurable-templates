package com.niloda.cot.simple.api

import arrow.core.Option.Companion.fromNullable
import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.simple.api.models.*
import com.niloda.cot.simple.repository.CotRepository
import com.niloda.cot.simple.repository.StoredCot
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Configure COT CRUD routes
 */
fun Route.cotRoutes(repository: CotRepository) {
    route("/api/cots") {
        // List all COTs
        get {
            repository
                .list()
                .fold(
                    { error -> internalServerError("InternalError", error.toString()) },
                    { cots -> ok(CotListResponse(cots.map { it.toSummary() })) }
                )
        }

        // Get single COT
        get("/{id}") {
            fromNullable(call.parameters["id"])
                .fold(
                    { badRequest("InvalidRequest", "id required") },
                    { id ->
                        repository
                            .findById(id)
                            .fold(
                                { error -> notFound("CotNotFound", error.toString()) },
                                { cot -> ok(cot.toDetailResponse()) }
                            )
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
                { error -> badRequest("InvalidDsl", error.toString()) },
                { cot ->
                    repository
                        .create(cot, request.dslCode)
                        .fold(
                            { error -> internalServerError("CreateError", error.toString()) },
                            { stored -> created(stored) }
                        )
                }
            )
        }

        // Update COT
        put("/{id}") {
            fromNullable(call.parameters["id"])
                .fold(
                    { badRequest("InvalidRequest", "Missing id parameter") },
                    { id ->

                        val request = call.receive<UpdateCotRequest>()

                        // Create a simple COT using the DSL
                        // For Phase 1, we create a basic static template
                        // Future phases will implement full DSL code parsing/evaluation
                        cot(request.name) {
                            "Template: ${request.name}\n".text
                            "DSL Code:\n${request.dslCode}\n".text
                        }.fold(
                            { error -> badRequest("InvalidDsl", error.toString()) },
                            { cot ->
                                repository
                                    .update(id, cot, request.dslCode)
                                    .fold(
                                        { error -> notFound("CotNotFound", error.toString()) },
                                        { stored -> ok(stored.toDetailResponse()) }
                                    )
                            }
                        )
                    }
                )
        }

        // Delete COT
        delete("/{id}") {
            fromNullable(call.parameters["id"])
                .fold(
                    { badRequest("InvalidRequest", "Missing id parameter") },
                    { id ->
                        repository
                            .delete(id)
                            .fold(
                                { error -> notFound("CotNotFound", error.toString()) },
                                { noContent() }
                            )
                    }
                )
        }
    }
}

private suspend fun RoutingContext.created(stored: StoredCot) {
    call.respond(HttpStatusCode.Created, stored.toDetailResponse())
}

private suspend fun RoutingContext.badRequest(error: String, message: String) {
    call.respond(BadRequest, ErrorResponse(error, message))
}

private suspend fun RoutingContext.internalServerError(error: String, message: String) {
    call.respond(InternalServerError, ErrorResponse(error, message))
}

private suspend fun RoutingContext.notFound(error: String, message: String) {
    call.respond(NotFound, ErrorResponse(error, message))
}

private suspend inline fun <reified T : Any> RoutingContext.ok(t: T) {
    call.respond(OK, t)
}

private suspend fun RoutingContext.noContent() {
    call.respond(HttpStatusCode.NoContent)
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
