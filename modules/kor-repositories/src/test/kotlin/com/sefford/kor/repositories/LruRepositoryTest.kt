/*
 * Copyright (C) 2017 Saúl Díaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sefford.kor.repositories

import com.sefford.kor.repositories.components.LruCache
import com.sefford.kor.repositories.components.Populator
import com.sefford.kor.repositories.components.Repository
import com.sefford.kor.repositories.components.RepositoryError
import com.sefford.kor.test.TestElement
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class LruRepositoryTest {

    lateinit var innerRepo: Repository<Int, TestElement>
    lateinit var repository: Repository<Int, TestElement>

    @Before
    fun setUp() {
        innerRepo = MemoryDataSource()
        repository = LruRepository(innerRepo, 3)
    }

    @Test
    fun `should persist an element`() {
        repository.save(TestElement(0))

        contains(0)
    }

    @Test
    fun `should persist in batch via varargs`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        contains(0)
        contains(1)
        contains(2)
    }

    @Test
    fun `should persist in batch via collection`() {
        repository.save(listOf(TestElement(0), TestElement(1), TestElement(2)))

        contains(0)
        contains(1)
        contains(2)
    }

    @Test
    fun `should persist in batch via iterator`() {
        repository.save(listOf(TestElement(0), TestElement(1), TestElement(2)).iterator())

        contains(0)
        contains(1)
        contains(2)
    }

    @Test
    fun `should not persist beyond its capacity in batch`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2), TestElement(3))

        doesNotContain(0)
        contains(1)
        contains(2)
        contains(3)
    }

    @Test
    fun `should not persist beyond its capacity`() {
        repository.save(TestElement(0))
        repository.save(TestElement(1))
        repository.save(TestElement(2))
        repository.save(TestElement(3))

        doesNotContain(0)
        contains(1)
        contains(2)
        contains(3)
    }

    @Test
    fun `contains should return true when the element is present`() {
        repository.save(TestElement(0))

        contains(0)
    }

    @Test
    fun `contains should return false when the element is not present`() {
        doesNotContain(0)
    }

    @Test
    fun `contains should return false and update the state when the element is not present in the inner repo`() {
        repository.save(TestElement(0))
        innerRepo.delete(0)

        doesNotContain(0)
    }

    @Test
    fun `should delete an element`() {
        repository.save(TestElement(0))
        repository.delete(0)

        doesNotContain(0)
    }

    @Test
    fun `should delete an element with the element`() {
        repository.save(TestElement(0))
        repository.delete(0, TestElement(0))

        doesNotContain(0)
    }

    @Test
    fun `should delete in batch via varargs`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))
        repository.delete(TestElement(0), TestElement(1), TestElement(2))

        doesNotContain(0)
        doesNotContain(1)
        doesNotContain(2)
    }

    @Test
    fun `should delete in batch via collection`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))
        repository.delete(listOf(TestElement(0), TestElement(1), TestElement(2)))

        doesNotContain(0)
        doesNotContain(1)
        doesNotContain(2)
    }

    @Test
    fun `should delete in batch via iterator`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))
        repository.delete(listOf(TestElement(0), TestElement(1), TestElement(2)).iterator())

        doesNotContain(0)
        doesNotContain(1)
        doesNotContain(2)
    }

    @Test
    fun `should retrieve an element`() {
        val originalElement = TestElement(0)
        repository.save(originalElement)

        repository[0].fold({ throw IllegalStateException("Expected a right projection") },
                { assertThat(it, `is`(originalElement)) })

    }

    @Test
    fun `should return NotFound error when the element is not in the inner repo`() {
        val originalElement = TestElement(0)
        repository.save(originalElement)
        innerRepo.delete(0)

        repository[0].fold({ assertTrue(it is RepositoryError.NotFound<*>) },
                { throw IllegalStateException("Expected a left projection") })

    }

    @Test
    fun `should retrieve in batch via varargs`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        val results = repository.get(0, 1, 2)

        assertThat(results.size, `is`(3))
        val ids = listOf(0, 1, 2)
        results.forEachIndexed { index, element -> assertThat(element.id, `is`(ids[index])) }
    }

    @Test
    fun `should retrieve in batch via collection`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        val results = repository.get(listOf(0, 1, 2))

        assertThat(results.size, `is`(3))
        val ids = listOf(0, 1, 2)
        results.forEachIndexed { index, element -> assertThat(element.id, `is`(ids[index])) }
    }

    @Test
    fun `should retrieve in batch via iterator`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        val results = repository.get(listOf(0, 1, 2).iterator())

        assertThat(results.size, `is`(3))
        val ids = listOf(0, 1, 2)
        results.forEachIndexed { index, element -> assertThat(element.id, `is`(ids[index])) }
    }

    @Test
    fun `should retrieve all`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        val results = repository.all

        assertThat(results.size, `is`(3))
        val ids = listOf(0, 1, 2)
        results.forEachIndexed { index, element -> assertThat(element.id, `is`(ids[index])) }
    }

    @Test
    fun `should properly be cleared`() {
        repository.save(TestElement(0), TestElement(1))

        repository.clear()

        doesNotContain(0)
        doesNotContain(1)
    }

    @Test
    fun `should report working when the components are properly initialized`() {
        assertTrue(repository.isReady)
    }

    @Test
    fun `should trigger populator on initialization`() {
        val populator = FakePopulator()
        repository = LruRepository(innerRepo, populator, 1)

        assertThat(populator.populatorExecutions, `is`(1))
    }

    internal fun contains(id: Int) {
        assertTrue(innerRepo.contains(id))
        assertTrue(repository.contains(id))
    }

    internal fun doesNotContain(id: Int) {
        assertFalse(innerRepo.contains(id))
        assertFalse(repository.contains(id))
    }

    class FakePopulator : Populator<Int> {

        var populatorExecutions: Int = 0

        override fun populate(lru: LruCache<Int>) {
            populatorExecutions++
        }

        override fun convert(name: String): Int = name.toInt()

    }
}
