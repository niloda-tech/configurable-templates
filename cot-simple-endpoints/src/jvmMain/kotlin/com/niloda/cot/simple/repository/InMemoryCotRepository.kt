package com.niloda.cot.simple.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.template.DomainError
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of CotRepository with concurrent-safe storage
 */
class InMemoryCotRepository : CotRepository {
    private val storage = ConcurrentHashMap<String, StoredCot>()
    
    override fun create(cot: Cot, dslCode: String): Either<DomainError, StoredCot> {
        val id = UUID.randomUUID().toString()
        val now = Instant.now()
        val stored = StoredCot(
            id = id,
            cot = cot,
            dslCode = dslCode,
            createdAt = now,
            updatedAt = now
        )
        storage[id] = stored
        return stored.right()
    }
    
    override fun findById(id: String): Either<DomainError, StoredCot> {
        return storage[id]?.right()
            ?: DomainError.MissingRequired("COT with id '$id' not found").left()
    }
    
    override fun update(id: String, cot: Cot, dslCode: String): Either<DomainError, StoredCot> {
        val existing = storage[id]
            ?: return DomainError.MissingRequired("COT with id '$id' not found").left()
        
        val updated = existing.copy(
            cot = cot,
            dslCode = dslCode,
            updatedAt = Instant.now()
        )
        storage[id] = updated
        return updated.right()
    }
    
    override fun delete(id: String): Either<DomainError, Unit> {
        return if (storage.remove(id) != null) {
            Unit.right()
        } else {
            DomainError.MissingRequired("COT with id '$id' not found").left()
        }
    }
    
    override fun list(): Either<DomainError, List<StoredCot>> {
        return storage.values.toList().right()
    }
}
