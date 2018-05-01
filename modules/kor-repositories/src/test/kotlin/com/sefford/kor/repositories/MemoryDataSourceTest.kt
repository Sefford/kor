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

import com.sefford.kor.repositories.components.RepoElement
import com.sefford.kor.repositories.components.Repository
import com.sefford.kor.test.TestElement
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations.initMocks

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class MemoryDataSourceTest : RepositoryTestSuite() {

    @Before
    fun setUp() {
        initMocks(this)
        repository = MemoryDataSource()
    }

    @Test
    fun `should update an element when it exists`() {
        val spy = SpyTestElement(EXPECTED_FIRST_ID)
        repository.save(spy)

        repository.save(TestElement(EXPECTED_FIRST_ID)).fold({ throw IllegalStateException("Expecting a right projection") },
                {
                    assertThat(it.id, `is`(EXPECTED_FIRST_ID))
                    assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(true))
                    assertThat(spy.updated, `is`(true))
                })
    }

    @Test
    fun `should not update an element when it exists and it is not updateable`() {
        val repository: Repository<Int, NonUpdateableTestElement> = MemoryDataSource()
        repository.save(NonUpdateableTestElement(EXPECTED_FIRST_ID))

        repository.save(NonUpdateableTestElement(EXPECTED_FIRST_ID)).fold(
                { throw IllegalStateException("Expecting a right projection") },
                {
                    assertThat(it.id, `is`(EXPECTED_FIRST_ID))
                    assertThat(repository.contains(EXPECTED_FIRST_ID), `is`(true))
                })
    }

    class NonUpdateableTestElement(override val id: Int) : RepoElement<Int>

    class SpyTestElement(id: Int) : TestElement(id) {

        var updated = false

        override fun update(other: TestElement): TestElement {
            updated = true
            return super.update(other)
        }
    }

}
