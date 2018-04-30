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

import com.nhaarman.mockito_kotlin.whenever
import com.sefford.kor.repositories.components.ExpirationPolicy
import com.sefford.kor.repositories.components.Repository
import com.sefford.kor.repositories.components.RepositoryError
import com.sefford.kor.test.TestElement
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import java.util.*

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class ExpirationRepositoryTest {

    lateinit var innerRepo: Repository<Int, TestElement>
    lateinit var repository: ExpirationRepository<Int, TestElement>

    @Mock
    lateinit var policy: ExpirationPolicy<Int>

    @Before
    fun setUp() {
        initMocks(this)

        innerRepo = MemoryDataSource(HashMap())
        repository = ExpirationRepository(innerRepo, policy)
    }

    @Test
    fun `clear should properly clear the repository`() {
        repository.save(TestElement(0))
        repository.save(TestElement(1))
        repository.save(TestElement(2))

        repository.clear()

        verify<ExpirationPolicy<Int>>(policy, times(1)).clear()
        assertFalse(innerRepo.contains(0))
        assertFalse(innerRepo.contains(1))
        assertFalse(innerRepo.contains(2))
    }

    @Test
    fun `contains should properly report false and delete when the element has expired`() {
        repository.save(TestElement(0))
        whenever(policy.isExpired(0)).thenReturn(true)

        assertFalse(repository.contains(0))
        verify<ExpirationPolicy<Int>>(policy, times(1)).notifyDeleted(0)
    }

    @Test
    fun `contains should properly report true when the element has not expired`() {
        repository.save(TestElement(0))
        whenever(policy.isExpired(0)).thenReturn(false)

        assertTrue(repository.contains(0))
        verify<ExpirationPolicy<Int>>(policy, never()).notifyDeleted(0)
    }

    @Test
    fun `contains should properly report false and delete when the element has not expired but it has magically been deleted from the repo`() {
        whenever(policy.isExpired(0)).thenReturn(false)

        assertFalse(repository.contains(0))

        verify<ExpirationPolicy<Int>>(policy, atLeastOnce()).notifyDeleted(0)
    }

    @Test
    fun `delete should properly update the status and delete the inner repo`() {
        repository.save(TestElement(0))

        repository.delete(0)

        verify(policy, times(1)).notifyDeleted(0)
        assertFalse(innerRepo.contains(0))
    }

    @Test
    fun `should delete in batch via varargs`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        repository.delete(TestElement(0), TestElement(1), TestElement(2))

        listOf(0, 1, 2).forEach { id ->
            verify(policy, times(1)).notifyDeleted(id)
            assertFalse(innerRepo.contains(id))
        }
    }

    @Test
    fun `should delete in batch via collection`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        repository.delete(listOf(TestElement(0), TestElement(1), TestElement(2)))

        listOf(0, 1, 2).forEach { id ->
            verify(policy, times(1)).notifyDeleted(id)
            assertFalse(innerRepo.contains(id))
        }
    }

    @Test
    fun `should delete in batch via iterator`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        repository.delete(listOf(TestElement(0), TestElement(1), TestElement(2)).iterator())

        listOf(0, 1, 2).forEach { id ->
            verify(policy, times(1)).notifyDeleted(id)
            assertFalse(innerRepo.contains(id))
        }
    }

    @Test
    fun `an element should be retrieved if it has not expired`() {
        repository.save(TestElement(0))
        whenever(policy.isExpired(0)).thenReturn(false)

        val testElement = repository[0].right().get()

        assertNotNull(testElement)
        assertThat(testElement.id, `is`(0))
    }

    @Test
    fun `a NotFound error should be returned if the element has expired`() {
        repository.save(TestElement(0))
        whenever(policy.isExpired(0)).thenReturn(true)

        val error = repository[0].left().get()

        assertTrue(error is RepositoryError.NotFound<*>)
    }

    @Test
    fun `only non-expired elements should be returned from varargs`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))
        whenever(policy.isExpired(1)).thenReturn(true)

        val elements = repository.get(0, 1, 2)

        assertThat(elements.size, `is`(2))
        assertTrue(elements.contains(TestElement(0)))
        assertTrue(elements.contains(TestElement(2)))
    }

    @Test
    fun `only non-expired elements should be returned from collection`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))
        whenever(policy.isExpired(1)).thenReturn(true)

        val elements = repository.get(listOf(0, 1, 2))

        assertThat(elements.size, `is`(2))
        assertTrue(elements.contains(TestElement(0)))
        assertTrue(elements.contains(TestElement(2)))
    }

    @Test
    fun `only non-expired elements should be returned from iterator`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))
        whenever(policy.isExpired(1)).thenReturn(true)

        val elements = repository.get(listOf(0, 1, 2).iterator())

        assertThat(elements.size, `is`(2))
        assertTrue(elements.contains(TestElement(0)))
        assertTrue(elements.contains(TestElement(2)))
    }

    @Test
    fun `only non-expired elements should be returned when retrieving all the elements`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        whenever(policy.isExpired(1)).thenReturn(true)

        val elements = repository.all

        assertThat(elements.size, `is`(2))
        assertTrue(elements.contains(TestElement(0)))
        assertTrue(elements.contains(TestElement(2)))
    }

    @Test
    fun `save should properly update the state and persis the element in the inner repo`() {
        repository.save(TestElement(0))

        verify<ExpirationPolicy<Int>>(policy, times(1)).notifyCreated(0)
        assertTrue(repository.contains(0))
    }

    @Test
    fun `elements should be able to be saved in batch via varargs`() {
        repository.save(TestElement(0), TestElement(1), TestElement(2))

        listOf(0, 1, 2).forEach { id ->
            assertThat(innerRepo.contains(id), `is`(true))
            verify(policy, times(1)).notifyCreated(id)
        }
    }

    @Test
    fun `elements should be able to be saved in batch via collection`() {
        repository.save(listOf(TestElement(0), TestElement(1), TestElement(2)))

        listOf(0, 1, 2).forEach { id ->
            assertThat(innerRepo.contains(id), `is`(true))
            verify(policy, times(1)).notifyCreated(id)
        }
    }

    @Test
    fun `elements should be able to be saved in batch via iterator`() {
        repository.save(listOf(TestElement(0), TestElement(1), TestElement(2)).iterator())

        listOf(0, 1, 2).forEach { id ->
            assertThat(innerRepo.contains(id), `is`(true))
            verify(policy, times(1)).notifyCreated(id)
        }
    }

    @Test
    fun `repository should report working if the elements are properly initialized`() {
        assertTrue(repository.isReady)
    }
}
