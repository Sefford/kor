package com.sefford.kor.usecases

import com.sefford.common.interfaces.Postable
import com.sefford.kor.usecases.test.utils.TestError
import com.sefford.kor.usecases.test.utils.TestResponse
import kotlinx.coroutines.experimental.async
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class StandaloneUseCaseTest {

    @Test
    fun `should execute synchronously`() {
        assertThat(TestStandaloneUseCase({ TestResponse() }).execute("").isRight(), `is`(true))
    }

    @Test
    fun `should execute synchronously and drop the results in the given postable when returns correctly`() {
        val givenAPostable = TestPostable()

        TestStandaloneUseCase({ TestResponse() }).execute(givenAPostable, "")

        assertThat(givenAPostable.messagesReceived(), `is`(1))
        assertThat(givenAPostable.message(0), instanceOf(TestResponse::class.java))
    }

    @Test
    fun `should execute synchronously and drop the results in the given postable when returns erroneously`() {
        val givenAPostable = TestPostable()

        TestStandaloneUseCase({ throw IllegalStateException() }).execute(givenAPostable, "")

        assertThat(givenAPostable.messagesReceived(), `is`(1))
        assertThat(givenAPostable.message(0), instanceOf(TestError::class.java))
    }

    @Test
    fun `should execute asynchronously`() {
        async {
            assertThat(TestStandaloneUseCase({ TestResponse() }).async("").isRight(), `is`(true))
        }
    }

    @Test
    fun `should execute asynchronously and drop the results in the given postable when returns correctly`() {
        val givenAPostable = TestPostable()

        async {
            TestStandaloneUseCase({ TestResponse() }).async(givenAPostable, "")

            assertThat(givenAPostable.messagesReceived(), `is`(1))
            assertThat(givenAPostable.message(0), instanceOf(TestResponse::class.java))
        }
    }

    @Test
    fun `should execute asynchronously and drop the results in the given postable when returns erroneously`() {
        val givenAPostable = TestPostable()

        async {
            TestStandaloneUseCase({ throw IllegalStateException() }).execute(givenAPostable, "")

            assertThat(givenAPostable.messagesReceived(), `is`(1))
            assertThat(givenAPostable.message(0), instanceOf(TestError::class.java))
        }
    }

    class TestStandaloneUseCase(val logic: () -> TestResponse) : StandaloneUseCase<Any, TestError, TestResponse> {
        override fun instantiate(params: Any): UseCase<TestError, TestResponse> {
            return UseCase.Execute<TestError, TestResponse>(logic)
                    .onError {
                        TestError()
                    }.build()
        }

    }

    class TestPostable : Postable {

        internal val events: MutableList<Any> = mutableListOf()

        override fun post(event: Any) {
            events.add(event)
        }

        fun messagesReceived(): Int {
            return events.size
        }

        fun message(i: Int): Any {
            return events[i]
        }

        fun noMessagesReceived(): Boolean {
            return events.isEmpty()
        }

    }
}
