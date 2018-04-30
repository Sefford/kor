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

import com.sefford.kor.repositories.components.Repository
import com.sefford.kor.repositories.components.RepositoryError
import com.sefford.kor.test.TestElement
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
@Ignore
open class RepositoryTestSuite {

    private val elements = listOf(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))
    private val ids = listOf(EXPECTED_FIRST_ID, EXPECTED_SECOND_ID, EXPECTED_THIRD_ID)
    private val partialIds = listOf(EXPECTED_FIRST_ID, EXPECTED_THIRD_ID)

    lateinit var repository: Repository<Int, TestElement>

    @Test
    fun `should save a element when it doesnt exist`() {
        val result = repository.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(result.right().get().id, `is`(EXPECTED_FIRST_ID))
        assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(true))
    }

    @Test
    fun `should persist all elements in batch via collection`() {
        val result = repository.save(elements)

        assertThat(result.size, `is`(EXPECTED_FULL_SAVE_SIZE))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(ids[index]))
        }
        result.forEachIndexed { index, element ->
            assertThat(repository.contains(ids[index]), `is`(true))
        }
    }

    @Test
    fun `should persist all elements in batch via varargs`() {
        val result = repository.save(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        assertThat(result.size, `is`(EXPECTED_FULL_SAVE_SIZE))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(ids[index]))
        }
        result.forEachIndexed { index, element ->
            assertThat(repository.contains(ids[index]), `is`(true))
        }
    }

    @Test
    fun `should indicate it does not contains when it does not contain`() {
        assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(false))
    }

    @Test
    fun `should indicate it does contain when it does contain`() {
        repository.save(TestElement(EXPECTED_FIRST_ID))

        assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(true))
    }

    @Test
    fun `should delete`() {
        repository.save(TestElement(EXPECTED_FIRST_ID))

        repository.delete(EXPECTED_FIRST_ID)

        assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(false))
    }

    @Test
    fun `should delete in batch via collection`() {
        repository.save(elements)

        repository.delete(elements)

        ids.forEach { assertThat(repository.contains(it), `is`(false)) }
    }

    @Test
    fun `should delete in batch via varargs`() {
        repository.save(elements)

        repository.delete(TestElement(EXPECTED_FIRST_ID), TestElement(EXPECTED_SECOND_ID), TestElement(EXPECTED_THIRD_ID))

        ids.forEach { assertThat(repository.contains(it), `is`(false)) }
    }

    @Test
    fun `should retrieve an element if it exists`() {
        repository.save(TestElement(EXPECTED_FIRST_ID))

        assertTrue(repository[EXPECTED_FIRST_ID].right().get().id == EXPECTED_FIRST_ID)
    }

    @Test
    fun `should return an error if it does not exist`() {
        assertTrue(repository[EXPECTED_FIRST_ID].left().get() is RepositoryError.NotFound<*>)
    }

    @Test
    fun `should clear the repo`() {
        repository.save(elements)

        repository.clear()

        assertThat(repository.all.isEmpty(), `is`(true))
    }

    @Test
    fun `should retrieve all elements from repo`() {
        repository.save(elements)

        repository.all.forEachIndexed { index, element ->
            assertThat(element.id, `is`(ids[index]))
        }
    }

    @Test
    fun `should report available`() {
        assertTrue(repository.isReady)
    }

    @Test
    fun `should retrieve only requested values from varargs`() {
        repository.save(elements)

        val result = repository.get(EXPECTED_FIRST_ID, EXPECTED_THIRD_ID)
        assertThat(result.size, `is`(2))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(partialIds[index]))
        }
    }

    @Test
    fun `should retrieve only requested values from list`() {
        repository.save(elements)

        val result = repository.get(partialIds)
        assertThat(result.size, `is`(EXPECTED_PARTIAL_SAVE_SIZE))
        result.forEachIndexed { index, element ->
            assertThat(element.id, `is`(partialIds[index]))
        }
    }

    companion object {
        const val EXPECTED_FIRST_ID = 1
        const val EXPECTED_SECOND_ID = 2
        const val EXPECTED_THIRD_ID = 3
        const val EXPECTED_FULL_SAVE_SIZE = 3
        const val EXPECTED_PARTIAL_SAVE_SIZE = 2
    }
}
