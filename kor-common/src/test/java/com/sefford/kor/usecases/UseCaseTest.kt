package com.sefford.kor.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrElse
import com.sefford.kor.errors.Error
import com.sefford.kor.repositories.utils.DummyPostable
import com.sefford.kor.responses.Response
import kotlinx.coroutines.experimental.yield
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException

class UseCaseTest {

    @Before
    fun setUp() {
    }

    @Test
    fun `should execute correctly`() {
        val useCase = UseCase.Execute<TestResponse, TestError> { Either.right(TestResponse()) }
                .onError { TestError() }.build()

        assertThat(useCase.execute<TestError, TestResponse>().isRight(), `is`(true))
    }

    @Test
    fun `should fail miserably`() {
        val useCase = UseCase.Execute<TestResponse, TestError> { Either.left(IOException("Catastrophic fail")) }
                .onError { TestError() }.build()

        assertThat(useCase.execute<TestError, TestResponse>().isLeft(), `is`(true))
    }

    @Test
    fun `should execute all phases`() {
        val response: TestResponse = UseCase.Execute<TestResponse, TestError> {
            val response = TestResponse()
            response.executed = true
            Either.right(response)
        }.process { response ->
            response.posprocessed = true
            Either.right(response)
        }.persist { response ->
            response.persisted = true
            Either.right(response)
        }.onError { TestError() }.build()
                .execute<TestError, TestResponse>().getOrElse { TestResponse() }

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