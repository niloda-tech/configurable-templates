package com.niloda.cot.simple.repository

import arrow.core.Either
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.template.DomainError

/**
 * Repository for storing and retrieving COTs
 */
interface CotRepository {
    /**
     * Create a new COT
     */
    fun create(cot: Cot, dslCode: String): Either<DomainError, StoredCot>
    
    /**
     * Find a COT by ID
     */
    fun findById(id: String): Either<DomainError, StoredCot>
    
    /**
     * Update an existing COT
     */
    fun update(id: String, cot: Cot, dslCode: String): Either<DomainError, StoredCot>
    
    /**
     * Delete a COT by ID
     */
    fun delete(id: String): Either<DomainError, Unit>
    
    /**
     * List all COTs
     */
    fun list(): Either<DomainError, List<StoredCot>>
}
