package com.niloda.cot.sandbox.service

import arrow.core.Either
import com.niloda.cot.sandbox.domain.SandboxError
import com.niloda.cot.sandbox.domain.SandboxRequest
import com.niloda.cot.sandbox.domain.SandboxResponse

/**
 * Service interface for executing COT templates in a sandboxed environment.
 */
interface SandboxService {
    /**
     * Execute a COT template in a sandboxed environment.
     * 
     * @param request The sandbox execution request containing the COT DSL code and parameters
     * @return Either a SandboxError or SandboxResponse with the generated output
     */
    fun execute(request: SandboxRequest): Either<SandboxError, SandboxResponse>
}
