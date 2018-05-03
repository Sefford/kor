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
package com.sefford.kor.usecases

import arrow.core.getOrHandle
import com.sefford.kor.usecases.components.Error
import com.sefford.kor.usecases.components.Response
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class UseCaseTest {

    @Before
    fun setUp() {
    }

    @Test
    fun `should execute correctly`() {
        val useCase = UseCase.Execute<TestError, TestResponse> { TestResponse() }
                .onError { TestError() }.build()

        assertThat(useCase.execute().isRight(), `is`(true))
    }

    @Test
    fun `should fail miserably`() {
        val useCase = UseCase.Execute<TestError, TestResponse> { throw IOException("Catastrophic fail") }
                .onError { TestError() }.build()

        assertThat(useCase.execute().isLeft(), `is`(true))
    }

    @Test
    fun `should execute all phases`() {
        UseCase.Execute<TestError, TestResponse> {
            val response = TestResponse()
            response.executed = true
            response
        }.process { response ->
            response.posprocessed = true
            response
        }.persist { response ->
            response.persisted = true
            response
        }.onError { TestError() }.build()
                .execute().fold({ fail() },
                        { response ->
                            assertThat(response.executed, `is`(true))
                            assertThat(response.posprocessed, `is`(true))
                            assertThat(response.persisted, `is`(true))
                        })
    }

    class TestResponse : Response {
        var executed = false
        var posprocessed = false
        var persisted = false

        override val success: Boolean
            get() = true
        override val fromNetwork: Boolean
            get() = true
    }

    class TestError : Error {
        override val statusCode: Int
            get() = 400
        override val userError: String
            get() = ""
        override val message: String
            get() = ""
        override val loggable: Boolean
            get() = false

    }
}
