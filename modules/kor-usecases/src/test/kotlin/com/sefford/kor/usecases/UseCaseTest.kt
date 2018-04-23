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

        useCase.execute().left().get()
        assertThat(useCase.execute().isLeft(), `is`(true))
    }

    @Test
    fun `should execute all phases`() {
        val response: TestResponse = UseCase.Execute<TestError, TestResponse> {
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
                .execute().getOrHandle { fail() as TestResponse }

        assertThat(response.executed, `is`(true))
        assertThat(response.posprocessed, `is`(true))
        assertThat(response.persisted, `is`(true))
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