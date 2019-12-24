package com.sefford.kor.usecases

import arrow.core.Either
import arrow.fx.IO
import com.sefford.kor.usecases.test.utils.TestError
import com.sefford.kor.usecases.test.utils.TestPostable
import com.sefford.kor.usecases.test.utils.TestResponse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.numerics.shouldBeExactly
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement

class StandaloneUseCaseTest : StringSpec({
    "should execute synchronously" {
        TestStandaloneUseCase { TestResponse() }.execute("").isRight().shouldBeTrue()
    }

    "should execute asynchornously" {
        TestStandaloneUseCase { TestResponse() }.async(Dispatchers.Unconfined, "") {
            it.map { result ->
                result.isRight().shouldBeTrue()
            }
        }
    }

    "should execute synchronously and drop the results in the given postable when returns correctly" {
        val givenAPostable = TestPostable()

        TestStandaloneUseCase { TestResponse() }.execute(givenAPostable, "")

        givenAPostable.shouldHaveReceivedOnlyAResponse()
    }

    "should execute synchronously and drop the results in the given postable when returns incorrectly" {
        val givenAPostable = TestPostable()

        TestStandaloneUseCase { throw IllegalStateException() }.execute(givenAPostable, "")

        givenAPostable.shouldHaveReceivedOnlyAnError()
    }

    "should execute asynchronously and drop the results in the given postable when returns correctly" {
        val givenAPostable = TestPostable()

        TestStandaloneUseCase { TestResponse() }.async(Dispatchers.Unconfined , givenAPostable, "")

        givenAPostable.shouldHaveReceivedOnlyAResponse()
    }

    "should execute asynchronously and drop the results in the given postable when returns erroneously" {
        val givenAPostable = TestPostable()

        TestStandaloneUseCase { throw IllegalStateException() }.async(Dispatchers.Unconfined, givenAPostable, "")

        givenAPostable.shouldHaveReceivedOnlyAnError()
    }

    "should return the proper defer object" {
        val computation = TestStandaloneUseCase { TestResponse() }.defer("")

        computation.shouldBeInstanceOf<IO<Either<TestError, TestResponse>>>()
        computation.unsafeRunSync().isRight().shouldBeTrue()
    }

    "should return the proper defer object when asking for a postable one" {
        val givenAPostable = TestPostable()

        val computation = TestStandaloneUseCase { TestResponse() }
                .defer(Dispatchers.Default, givenAPostable, "")

        computation.shouldBeInstanceOf<IO<Unit>>()

        computation.unsafeRunSync()
        givenAPostable.shouldHaveReceivedOnlyAResponse()
    }

}) {

    class TestStandaloneUseCase(val logic: () -> TestResponse) : StandaloneUseCase<Any, TestError, TestResponse> {
        override fun instantiate(params: Any): UseCase<TestError, TestResponse> {
            return UseCase.Execute<TestError, TestResponse>(logic)
                    .onError {
                        TestError()
                    }.build()
        }
    }
}

private fun TestPostable.shouldHaveReceivedOnlyAResponse() {
    messagesReceived() shouldBeExactly 1
    message(0).shouldBeInstanceOf<TestResponse>()
}

private fun TestPostable.shouldHaveReceivedOnlyAnError() {
    messagesReceived() shouldBeExactly 1
    message(0).shouldBeInstanceOf<TestError>()
}
