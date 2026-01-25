package com.niloda.cot.simple.repository

import arrow.core.Either
import com.niloda.cot.domain.Cot
import com.niloda.cot.domain.template.DomainError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class InMemoryCotRepositoryTest {

    private lateinit var repository: InMemoryCotRepository

    @BeforeEach
    fun setUp() {
        repository = InMemoryCotRepository()
    }

    @Test
    fun `create stores COT and returns success`() {
        val cot = Cot(name = "TestCot", schema = emptyList())
        val dslCode = "cot(\"TestCot\") { }"

        val result = repository.create(cot, dslCode)

        assertTrue(result.isRight())
        val stored = result.getOrNull()!!
        assertEquals("TestCot", stored.cot.name)
        assertEquals(dslCode, stored.dslCode)
        assertNotNull(stored.id)
        assertNotNull(stored.createdAt)
        assertNotNull(stored.updatedAt)
        assertEquals(stored.createdAt, stored.updatedAt)
    }

    @Test
    fun `create generates unique IDs for each COT`() {
        val cot1 = Cot(name = "Cot1", schema = emptyList())
        val cot2 = Cot(name = "Cot2", schema = emptyList())

        val stored1 = repository.create(cot1, "code1").getOrNull()!!
        val stored2 = repository.create(cot2, "code2").getOrNull()!!

        assertNotEquals(stored1.id, stored2.id)
    }

    @Test
    fun `findById returns COT when it exists`() {
        val cot = Cot(name = "TestCot", schema = emptyList())
        val stored = repository.create(cot, "code").getOrNull()!!

        val result = repository.findById(stored.id)

        assertTrue(result.isRight())
        val found = result.getOrNull()!!
        assertEquals(stored.id, found.id)
        assertEquals(stored.cot.name, found.cot.name)
        assertEquals(stored.dslCode, found.dslCode)
    }

    @Test
    fun `findById returns error when COT does not exist`() {
        val result = repository.findById("nonexistent-id")

        assertTrue(result.isLeft())
        val error = result.swap().getOrNull()!!
        assertTrue(error is DomainError.MissingRequired)
        assertTrue((error as DomainError.MissingRequired).what.contains("nonexistent-id"))
    }

    @Test
    fun `update modifies existing COT and updates timestamp`() {
        val cot = Cot(name = "Original", schema = emptyList())
        val stored = repository.create(cot, "original code").getOrNull()!!
        val originalUpdatedAt = stored.updatedAt

        // Small delay to ensure timestamp changes
        Thread.sleep(10)

        val updatedCot = Cot(name = "Updated", schema = emptyList())
        val result = repository.update(stored.id, updatedCot, "updated code")

        assertTrue(result.isRight())
        val updated = result.getOrNull()!!
        assertEquals(stored.id, updated.id)
        assertEquals("Updated", updated.cot.name)
        assertEquals("updated code", updated.dslCode)
        assertEquals(stored.createdAt, updated.createdAt)
        assertTrue(updated.updatedAt.isAfter(originalUpdatedAt))
    }

    @Test
    fun `update returns error when COT does not exist`() {
        val cot = Cot(name = "Test", schema = emptyList())
        val result = repository.update("nonexistent-id", cot, "code")

        assertTrue(result.isLeft())
        val error = result.swap().getOrNull()!!
        assertTrue(error is DomainError.MissingRequired)
    }

    @Test
    fun `delete removes COT and returns success`() {
        val cot = Cot(name = "TestCot", schema = emptyList())
        val stored = repository.create(cot, "code").getOrNull()!!

        val deleteResult = repository.delete(stored.id)

        assertTrue(deleteResult.isRight())
        
        // Verify COT is actually deleted
        val findResult = repository.findById(stored.id)
        assertTrue(findResult.isLeft())
    }

    @Test
    fun `delete returns error when COT does not exist`() {
        val result = repository.delete("nonexistent-id")

        assertTrue(result.isLeft())
        val error = result.swap().getOrNull()!!
        assertTrue(error is DomainError.MissingRequired)
    }

    @Test
    fun `list returns empty list when no COTs exist`() {
        val result = repository.list()

        assertTrue(result.isRight())
        val list = result.getOrNull()!!
        assertTrue(list.isEmpty())
    }

    @Test
    fun `list returns all stored COTs`() {
        val cot1 = Cot(name = "Cot1", schema = emptyList())
        val cot2 = Cot(name = "Cot2", schema = emptyList())
        val cot3 = Cot(name = "Cot3", schema = emptyList())

        repository.create(cot1, "code1")
        repository.create(cot2, "code2")
        repository.create(cot3, "code3")

        val result = repository.list()

        assertTrue(result.isRight())
        val list = result.getOrNull()!!
        assertEquals(3, list.size)
        
        val names = list.map { it.cot.name }.toSet()
        assertTrue(names.contains("Cot1"))
        assertTrue(names.contains("Cot2"))
        assertTrue(names.contains("Cot3"))
    }

    @Test
    fun `list does not include deleted COTs`() {
        val cot1 = Cot(name = "Cot1", schema = emptyList())
        val cot2 = Cot(name = "Cot2", schema = emptyList())
        
        val stored1 = repository.create(cot1, "code1").getOrNull()!!
        repository.create(cot2, "code2")
        
        repository.delete(stored1.id)

        val result = repository.list()

        assertTrue(result.isRight())
        val list = result.getOrNull()!!
        assertEquals(1, list.size)
        assertEquals("Cot2", list[0].cot.name)
    }

    @Test
    fun `concurrent creates handle multiple threads safely`() {
        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val results = mutableListOf<Either<DomainError, StoredCot>>()

        val threads = (1..threadCount).map { i ->
            thread {
                val cot = Cot(name = "ConcurrentCot$i", schema = emptyList())
                val result = repository.create(cot, "code$i")
                synchronized(results) {
                    results.add(result)
                }
                latch.countDown()
            }
        }

        latch.await(5, TimeUnit.SECONDS)
        threads.forEach { it.join() }

        // All creates should succeed
        assertEquals(threadCount, results.size)
        assertTrue(results.all { it.isRight() })

        // All IDs should be unique
        val ids = results.mapNotNull { it.getOrNull()?.id }.toSet()
        assertEquals(threadCount, ids.size)

        // Repository should contain all COTs
        val list = repository.list().getOrNull()!!
        assertEquals(threadCount, list.size)
    }

    @Test
    fun `concurrent updates to different COTs do not interfere`() {
        // Create initial COTs
        val cot1 = Cot(name = "Cot1", schema = emptyList())
        val cot2 = Cot(name = "Cot2", schema = emptyList())
        val stored1 = repository.create(cot1, "code1").getOrNull()!!
        val stored2 = repository.create(cot2, "code2").getOrNull()!!

        val latch = CountDownLatch(2)
        val results = mutableListOf<Either<DomainError, StoredCot>>()

        val thread1 = thread {
            val updated = Cot(name = "UpdatedCot1", schema = emptyList())
            val result = repository.update(stored1.id, updated, "updated1")
            synchronized(results) {
                results.add(result)
            }
            latch.countDown()
        }

        val thread2 = thread {
            val updated = Cot(name = "UpdatedCot2", schema = emptyList())
            val result = repository.update(stored2.id, updated, "updated2")
            synchronized(results) {
                results.add(result)
            }
            latch.countDown()
        }

        latch.await(5, TimeUnit.SECONDS)
        thread1.join()
        thread2.join()

        // Both updates should succeed
        assertEquals(2, results.size)
        assertTrue(results.all { it.isRight() })

        // Verify both COTs are updated correctly
        val found1 = repository.findById(stored1.id).getOrNull()!!
        val found2 = repository.findById(stored2.id).getOrNull()!!
        assertEquals("UpdatedCot1", found1.cot.name)
        assertEquals("UpdatedCot2", found2.cot.name)
    }

    @Test
    fun `concurrent deletes handle race conditions correctly`() {
        val cot = Cot(name = "TestCot", schema = emptyList())
        val stored = repository.create(cot, "code").getOrNull()!!

        val threadCount = 5
        val latch = CountDownLatch(threadCount)
        val results = mutableListOf<Either<DomainError, Unit>>()

        val threads = (1..threadCount).map {
            thread {
                val result = repository.delete(stored.id)
                synchronized(results) {
                    results.add(result)
                }
                latch.countDown()
            }
        }

        latch.await(5, TimeUnit.SECONDS)
        threads.forEach { it.join() }

        // Exactly one delete should succeed, rest should fail
        val successes = results.count { it.isRight() }
        val failures = results.count { it.isLeft() }
        
        assertEquals(1, successes)
        assertEquals(threadCount - 1, failures)

        // COT should be deleted
        val findResult = repository.findById(stored.id)
        assertTrue(findResult.isLeft())
    }
}
