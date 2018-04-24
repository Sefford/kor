/*
 * Copyright (C) 2018 Saúl Díaz
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
import arrow.core.Right
import com.sefford.kor.repositories.components.RepositoryError
import com.sefford.kor.repositories.components.StubDataSource
import com.sefford.kor.test.TestElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class StubDataSourceTest {
    private val elements = listOf(TestElement(RepositoryTestSuite.EXPECTED_FIRST_ID), TestElement(RepositoryTestSuite.EXPECTED_SECOND_ID), TestElement(RepositoryTestSuite.EXPECTED_THIRD_ID))
    private val ids = listOf(RepositoryTestSuite.EXPECTED_FIRST_ID, RepositoryTestSuite.EXPECTED_SECOND_ID, RepositoryTestSuite.EXPECTED_THIRD_ID)
    private val partialIds = listOf(RepositoryTestSuite.EXPECTED_FIRST_ID, RepositoryTestSuite.EXPECTED_THIRD_ID)

    lateinit var dataSource: StubDataSourceImpl
    @Before
    fun setUp() {
        dataSource = StubDataSourceImpl()
    }

    @Test
    fun `should persist all elements in batch from collection`() {
        val result = dataSource.save(elements)

        assertPersistance(result)
    }

    @Test
    fun `should persist all elements in batch from varargs`() {
        val result = dataSource.save(TestElement(RepositoryTestSuite.EXPECTED_FIRST_ID), TestElement(RepositoryTestSuite.EXPECTED_SECOND_ID), TestElement(RepositoryTestSuite.EXPECTED_THIRD_ID))

        assertPersistance(result)
    }

    @Test
    fun `should persist all elements in batch from iterator`() {
        val result = dataSource.save(elements.iterator())

        assertPersistance(result)
    }

    @Test
    fun `should delete in batch from collection`() {
        dataSource.save(elements)

        dataSource.delete(elements)

        ids.forEach { assertThat(dataSource.deletions.contains(it), `is`(true)) }
    }

    @Test
    fun `should delete in batch from varargs`() {
        dataSource.save(elements)

        dataSource.delete(TestElement(RepositoryTestSuite.EXPECTED_FIRST_ID), TestElement(RepositoryTestSuite.EXPECTED_SECOND_ID), TestElement(RepositoryTestSuite.EXPECTED_THIRD_ID))

        ids.forEach { assertThat(dataSource.deletions.contains(it), `is`(true)) }
    }

    @Test
    fun `should delete in batch from iterator`() {
        dataSource.save(elements)

        dataSource.delete(elements.iterator())

        ids.forEach { assertThat(dataSource.deletions.contains(it), `is`(true)) }
    }

    @Test
    fun `should retrieve only requested values from varargs`() {
        dataSource.save(elements)

        val result = dataSource.get(RepositoryTestSuite.EXPECTED_FIRST_ID, RepositoryTestSuite.EXPECTED_THIRD_ID)
        assertThat(result.size, `is`(2))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(partialIds[index]))
        }
    }

    @Test
    fun `should retrieve only requested values from list`() {
        dataSource.save(elements)

        val result = dataSource.get(partialIds)
        assertThat(result.size, `is`(RepositoryTestSuite.EXPECTED_PARTIAL_SAVE_SIZE))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(partialIds[index]))
        }
    }


    @Test
    fun `should retrieve only requested values from iterator`() {
        dataSource.save(elements)

        val result = dataSource.get(partialIds.iterator())
        assertThat(result.size, `is`(2))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(partialIds[index]))
        }
    }

    private fun assertPersistance(result: Collection<TestElement>) {
        assertThat(result.size, `is`(RepositoryTestSuite.EXPECTED_FULL_SAVE_SIZE))
        result.forEachIndexed { index, element ->
            assertThat(dataSource.storages.contains(element.id), `is`(true))
        }
    }


    class StubDataSourceImpl : StubDataSource<Int, TestElement> {
        val deletions: MutableSet<Int> = mutableSetOf()
        val storages: MutableSet<Int> = mutableSetOf()

        override val all: Collection<TestElement>
            get() = emptyList()
        override val isReady: Boolean
            get() = true

        override fun clear() {
        }

        override fun contains(id: Int): Boolean {
            return true
        }

        override fun delete(id: Int, element: TestElement) {
            deletions.add(id)
        }

        override fun delete(id: Int) {
            deletions.add(id)
        }

        override fun get(id: Int): Either<RepositoryError, TestElement> {
            return Right(TestElement(id))
        }

        override fun save(element: TestElement): Either<RepositoryError, TestElement> {
            storages.add(element.id)
            return Right(element)
        }

    }
}