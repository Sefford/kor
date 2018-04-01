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

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.nhaarman.mockito_kotlin.*
import com.sefford.kor.interactors.RepositoryError
import com.sefford.kor.repositories.interfaces.Repository
import com.sefford.kor.repositories.utils.TestElement
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.initMocks

/**
 * @author Saul Diaz <sefford></sefford>@gmail.com>
 */
class TwoTierRepositoryTest {

    lateinit var firstLevel: Repository<Int, TestElement>
    lateinit var nextLevel: Repository<Int, TestElement>
    lateinit var mockedRepository: Repository<Int, TestElement>

    lateinit var repository: TwoTierRepository<Int, TestElement>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initMocks(this)

        firstLevel = MemoryDataSource()
        nextLevel = MemoryDataSource()
        mockedRepository = mock(nextLevel::class.java)

        repository = TwoTierRepository(firstLevel, nextLevel)
    }

    @Test
    fun `should return next level is not available`() {
        repository = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))

        assertFalse(repository.hasNextLevel())
    }

    @Test
    fun `should report the next level is available`() {
        assertTrue(repository.hasNextLevel())
    }

    @Test
    fun `should persist in current level when next level is not available`() {
        repository = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))

        val result = repository.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(result.isRight(), `is`(true))
        assertThat(result.right().get().id, `is`(EXPECTED_FIRST_ID))
        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(true))
        assertThat(nextLevel.contains(EXPECTED_FIRST_ID), `is`(false))
    }

    @Test
    fun `should persist in next level if current level is not available`() {
        repository = TwoTierRepository(DeactivatedRepository(firstLevel), nextLevel)

        val result = repository.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(result.isRight(), `is`(true))
        assertThat(result.right().get().id, `is`(EXPECTED_FIRST_ID))
        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(false))
        assertThat(nextLevel.contains(EXPECTED_FIRST_ID), `is`(true))
    }

    @Test
    fun `should persist on both levels`() {
        val result = repository.save(TestElement(EXPECTED_FIRST_ID))
        assertThat(result.isRight(), `is`(true))
        assertThat(result.right().get().id, `is`(EXPECTED_FIRST_ID))
        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(true))
        assertThat(nextLevel.contains(EXPECTED_FIRST_ID), `is`(true))
    }

    @Test
    fun `should return NotReady error when the repository is not ready`() {
        repository = TwoTierRepository(DeactivatedRepository(firstLevel), DeactivatedRepository(nextLevel))

        val result = repository.save(TestElement(EXPECTED_FIRST_ID))
        assertTrue(result.left().get() is RepositoryError.NotReady)
    }

    @Test
    fun `contains should return false if none of the levels contains the element`() {
        assertFalse(repository.contains(EXPECTED_FIRST_ID))
    }

    @Test
    fun `contains should return true if only the first level contain the element`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))

        assertTrue(repository.contains(EXPECTED_FIRST_ID))
    }

    @Test
    fun `contains should return true if only the second level contains the element`() {
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))

        assertTrue(repository.contains(EXPECTED_FIRST_ID))
    }

    @Test
    fun `contains should return true if both levels contain the element`() {
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))

        assertTrue(repository.contains(EXPECTED_FIRST_ID))
    }

    @Test
    fun `contains should return false if the first level does not contain the element and the second is unavailable`() {
        repository = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))

        assertFalse(repository.contains(EXPECTED_FIRST_ID))
    }

    @Test
    fun `contains should return true if the first level contains the element and the second is unavailable`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))
        repository = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))

        assertTrue(repository.contains(EXPECTED_FIRST_ID))
    }

    @Test
    fun `should delete only in the first level if the next level is not available`() {
        val deactivatedRepository = DeactivatedRepository(nextLevel)
        repository = TwoTierRepository(firstLevel, deactivatedRepository)
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))

        repository.delete(EXPECTED_FIRST_ID, TestElement(EXPECTED_FIRST_ID))

        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(false))
        assertThat(deactivatedRepository.deleteCounter, `is`(0))
    }

    @Test
    fun `should delete only in the first level if the next level is not available with id only`() {
        val deactivatedRepo = DeactivatedRepository(nextLevel)
        repository = TwoTierRepository(firstLevel, deactivatedRepo)
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))

        repository.delete(EXPECTED_FIRST_ID)

        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(false))
        assertThat(deactivatedRepo.deleteCounter, `is`(0))
    }

    @Test
    fun `should delete in both levels`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))

        repository.delete(EXPECTED_FIRST_ID)

        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(false))
        assertThat(nextLevel.contains(EXPECTED_FIRST_ID), `is`(false))
    }

    @Test
    fun `should delete in both levels with id only`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))

        repository.delete(EXPECTED_FIRST_ID)

        assertThat(firstLevel.contains(EXPECTED_FIRST_ID), `is`(false))
        assertThat(nextLevel.contains(EXPECTED_FIRST_ID), `is`(false))
    }

    @Test
    fun `should delete all elements via iterator`() {
        repository.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        repository.delete(listOf(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID)).iterator())

        assertThat(repository.all.isEmpty(), `is`(true))
    }

    @Test
    fun `should delete all elements via collection`() {
        repository.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        repository.delete(listOf(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID)))

        assertThat(repository.all.isEmpty(), `is`(true))
    }

    @Test
    fun `should delete all elements via varargs`() {
        repository.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        repository.delete(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        assertThat(repository.all.isEmpty(), `is`(true))
    }

    @Test
    fun `should return not found if both levels do not have the element`() {
        assertTrue(repository[EXPECTED_FIRST_ID].left().get() is RepositoryError.NotFound<*>)
    }

    @Test
    fun `should return not found if first level do not have the element and second is unavailable`() {
        repository = TwoTierRepository(firstLevel, mockedRepository)

        assertTrue(repository[EXPECTED_FIRST_ID].left().get() is RepositoryError.NotFound<*>)
        verify(mockedRepository, never()).get(EXPECTED_FIRST_ID)
    }

    @Test
    fun `should return NotReady error if the repository is not ready`() {
        repository = TwoTierRepository(mockedRepository, mockedRepository)

        assertTrue(repository[EXPECTED_FIRST_ID].left().get() is RepositoryError.NotReady)
    }

    @Test
    fun `should return the element if the first level has the element and the second is unavailable`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))
        repository = TwoTierRepository(firstLevel, mockedRepository)

        assertThat(repository[EXPECTED_FIRST_ID].right().get().id, `is`(EXPECTED_FIRST_ID))
    }

    @Test
    fun `should return the element if the first level has the element`() {
        repository.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(repository[EXPECTED_FIRST_ID].right().get().id, `is`(EXPECTED_FIRST_ID))
    }

    @Test
    fun `should return the element if the second level has the element`() {
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(repository[EXPECTED_FIRST_ID].right().get().id, `is`(EXPECTED_FIRST_ID))
    }

    @Test
    fun `should recover if the first level has a problem retrieving the element`() {
        repository = TwoTierRepository(mockedRepository, nextLevel)
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))
        whenever(mockedRepository.contains(EXPECTED_FIRST_ID)).thenReturn(true)
        whenever(mockedRepository[EXPECTED_FIRST_ID]).thenReturn(Left(RepositoryError.CannotRetrieve(Exception())))

        assertThat(repository[EXPECTED_FIRST_ID].right().get().id, `is`(EXPECTED_FIRST_ID))
    }

    @Test
    fun `should retrieve the element if both levels have it`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID))
        nextLevel.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(repository[EXPECTED_FIRST_ID].right().get().id, `is`(EXPECTED_FIRST_ID))
    }

    @Test
    fun `should be able to retrieve in batch via varargs`() {
        firstLevel.save(FULL_ELEMENT_LIST)
        nextLevel.save(FULL_ELEMENT_LIST)

        val result = repository.get(EXPECTED_FIRST_ID, EXPECTED_SECOND_ID, EXPECTED_THIRD_ID)

        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element -> FULL_IDS_LIST.contains(element.id) }
    }

    @Test
    fun `should be able to retrieve in batch via collection`() {
        firstLevel.save(FULL_ELEMENT_LIST)
        nextLevel.save(FULL_ELEMENT_LIST)

        val result = repository.get(listOf(EXPECTED_FIRST_ID, EXPECTED_SECOND_ID, EXPECTED_THIRD_ID))

        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element -> FULL_IDS_LIST.contains(element.id) }
    }

    @Test
    fun `should be able to retrieve in batch via iterator`() {
        firstLevel.save(FULL_ELEMENT_LIST)
        nextLevel.save(FULL_ELEMENT_LIST)

        val result = repository.get(listOf(EXPECTED_FIRST_ID, EXPECTED_SECOND_ID, EXPECTED_THIRD_ID).iterator())

        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element -> FULL_IDS_LIST.contains(element.id) }
    }

    @Test
    fun `should persist all elements if second level is unavailable via iterator`() {
        repository = TwoTierRepository(firstLevel, mockedRepository)

        repository.save(FULL_ELEMENT_LIST.iterator())

        FULL_IDS_LIST.forEach { id: Int -> assertThat(firstLevel.contains(id), `is`(true)) }
        verify(mockedRepository, never()).save(any<TestElement>())
    }

    @Test
    fun `should persist all elements if second level is unavailable via collection`() {
        repository = TwoTierRepository(firstLevel, mockedRepository)

        repository.save(FULL_ELEMENT_LIST)

        FULL_IDS_LIST.forEach { id: Int -> assertThat(firstLevel.contains(id), `is`(true)) }
        verify(mockedRepository, never()).save(any<TestElement>())
    }

    @Test
    fun `should persist all elements if second level is unavailable via varargs`() {
        repository = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))

        repository.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        FULL_IDS_LIST.forEach { id: Int ->
            assertThat(firstLevel.contains(id), `is`(true))
            assertThat(nextLevel.contains(id), `is`(false))
        }
    }

    @Test
    fun `should persist all elements via iterator`() {
        repository.save(FULL_ELEMENT_LIST.iterator())

        FULL_IDS_LIST.forEach { id: Int ->
            assertThat(firstLevel.contains(id), `is`(true))
            assertThat(nextLevel.contains(id), `is`(true))
        }
    }

    @Test
    fun `should persist all elements via collection`() {
        repository.save(FULL_ELEMENT_LIST)

        FULL_IDS_LIST.forEach { id: Int ->
            assertThat(firstLevel.contains(id), `is`(true))
            assertThat(nextLevel.contains(id), `is`(true))
        }
    }

    @Test
    fun `should persist all elements via varargs`() {
        repository.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        FULL_IDS_LIST.forEach { id: Int ->
            assertThat(firstLevel.contains(id), `is`(true))
            assertThat(nextLevel.contains(id), `is`(true))
        }
    }

    @Test
    fun `should clear first level when second level is not available`() {
        val nextLevelMockedRepository = mock(nextLevel::class.java)
        repository = TwoTierRepository(mockedRepository, nextLevelMockedRepository)

        repository.clear()

        verify(mockedRepository, times(1)).clear()
        verify(nextLevelMockedRepository, never()).clear()
    }

    @Test
    fun `should clear both levels`() {
        val nextLevelMockedRepository = mock(nextLevel::class.java)
        whenever(nextLevelMockedRepository.isReady).doReturn(true)
        repository = TwoTierRepository(mockedRepository, nextLevelMockedRepository)

        repository.clear()
        verify(mockedRepository, times(1)).clear()
        verify(nextLevelMockedRepository, times(1)).clear()
    }

    @Test
    fun `should retrieve all elements when second level is not available`() {
        repository = TwoTierRepository(firstLevel, mockedRepository)
        firstLevel.save(FULL_ELEMENT_LIST)

        val result = repository.all
        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element: TestElement -> assertThat(FULL_IDS_LIST.contains(element.id), `is`(true)) }
        verify(mockedRepository, never()).all
    }

    @Test
    fun `should retrieve all elements from second level if first does not have it`() {
        nextLevel.save(FULL_ELEMENT_LIST)

        val result = repository.all
        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element: TestElement -> assertThat(FULL_IDS_LIST.contains(element.id), `is`(true)) }
    }

    @Test
    fun `should merge elements when retrieving all elements between both levels`() {
        firstLevel.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_THIRD_ID))
        nextLevel.save(TestElement(EXPECTED_SECOND_ID))

        val result = repository.all
        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element: TestElement -> assertThat(FULL_IDS_LIST.contains(element.id), `is`(true)) }
    }

    @Test
    fun `should not duplicate elements when retrieving all elements between both levels`() {
        firstLevel.save(FULL_ELEMENT_LIST)
        nextLevel.save(FULL_ELEMENT_LIST)

        val result = repository.all
        assertThat(result.size, `is`(EXPECTED_FULL_LIST_SIZE))
        result.forEach { element: TestElement -> assertThat(FULL_IDS_LIST.contains(element.id), `is`(true)) }
    }

    @Test
    fun `should report ready when both levels are ready`() {
        assertTrue(repository.isReady)
    }

    @Test
    fun `should report ready when only first level is ready`() {
        repository = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))

        assertTrue(repository.isReady)
    }

    @Test
    fun `should report ready when only second level is ready`() {
        repository = TwoTierRepository(DeactivatedRepository(firstLevel), nextLevel)

        assertTrue(repository.isReady)
    }

    @Test
    fun `should report not ready when both levels are not ready`() {
        repository = TwoTierRepository(DeactivatedRepository(firstLevel), DeactivatedRepository(nextLevel))

        assertFalse(repository.isReady)
    }

    @Test
    fun `a three tier should resolve the element correctly when the first level does not have it, the second is not ready and the third has it`() {
        val thirdLevel: Repository<Int, TestElement> = MemoryDataSource()
        thirdLevel.save(TestElement(EXPECTED_FIRST_ID))

        val localLevel = TwoTierRepository(firstLevel, DeactivatedRepository(nextLevel))
        val repository = TwoTierRepository(localLevel, thirdLevel)

        assertThat(repository[EXPECTED_FIRST_ID].right().get().id, `is`(EXPECTED_FIRST_ID))
    }

    @Test
    fun `a three tier should resolve contains correctly when the first level does not have it, the second is not ready and the third has it`() {
        val firstLevel = mock(firstLevel::class.java)
        val secondLevel = mock(firstLevel::class.java)
        val thirdLevel = mock(firstLevel::class.java)

        val localLevel = TwoTierRepository(firstLevel, secondLevel)
        val repository = TwoTierRepository(localLevel, thirdLevel)

        whenever(firstLevel.contains(EXPECTED_FIRST_ID)).thenReturn(false)
        whenever(firstLevel.isReady).thenReturn(true)
        whenever(secondLevel.contains(EXPECTED_FIRST_ID)).thenReturn(false)
        whenever(secondLevel.isReady).thenReturn(false)
        whenever(thirdLevel.isReady).thenReturn(true)
        whenever(thirdLevel.contains(EXPECTED_FIRST_ID)).thenReturn(true)

        assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(true))
    }

    companion object {
        private const val EXPECTED_FIRST_ID = 1
        private const val EXPECTED_SECOND_ID = 2
        private const val EXPECTED_THIRD_ID = 3
        private const val EXPECTED_FULL_LIST_SIZE = 3
        private val FULL_IDS_LIST = mutableListOf(EXPECTED_FIRST_ID, EXPECTED_SECOND_ID, EXPECTED_THIRD_ID)
        private val FULL_ELEMENT_LIST = mutableListOf(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))
    }

    class DeactivatedRepository(repository: Repository<Int, TestElement>) : Repository<Int, TestElement> by repository {

        var deleteCounter = 0

        override val isReady: Boolean
            get() = false

        override fun save(element: TestElement): Either<RepositoryError, TestElement> {
            return Left(RepositoryError.NotReady)
        }

        override fun get(id: Int): Either<RepositoryError, TestElement> {
            return Left(RepositoryError.NotReady)
        }

        override fun delete(id: Int) {
            deleteCounter++
        }

        override fun delete(id: Int, element: TestElement) {
            deleteCounter++
        }
    }
}