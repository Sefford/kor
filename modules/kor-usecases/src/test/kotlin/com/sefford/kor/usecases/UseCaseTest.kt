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

import arrow.core.Either
import arrow.fx.IO
import com.sefford.kor.usecases.components.PerformanceModule
import com.sefford.kor.usecases.test.utils.TestError
import com.sefford.kor.usecases.test.utils.TestResponse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.junit.Assert.fail
import java.io.IOException

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class UseCaseTest : StringSpec({
    "should execute correctly" {
        val useCase = UseCase.Execute<TestError, TestResponse> { TestResponse() }
                .onError { TestError() }.build()

        useCase.execute().isRight().shouldBeTrue()
    }
    "should fail miserably" {
        val useCase = UseCase.Execute<TestError, TestResponse> { throw IOException("Catastrophic fail") }
                .onError { TestError() }.build()

        useCase.execute().isLeft().shouldBeTrue()
    }

    "should properly fail in the postprocssor" {
        UseCase.Execute<TestError, TestResponse> {
            val response = TestResponse()
            response.executed = true
            response
        }.process { response ->
            throw IOException("Catastrophic fail")
        }.persist { response ->
            response.persisted = true
            response
        }.onError { TestError(it) }.build()
                .execute().fold({ error ->
                    error.exception.shouldBeInstanceOf<IOException>()
                }, { fail() })
    }

    "should execute all phases" {
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
        }.onError {
            TestError()
        }.build().execute().fold({ fail() },
                { response ->
                    response.executed.shouldBeTrue()
                    response.posprocessed.shouldBeTrue()
                    response.persisted.shouldBeTrue()
                })
    }

    "should call the performance module during correct execution" {
        val performanceModule = TestPerformanceModule()

        UseCase.Execute<TestError, TestResponse> { TestResponse() }
                .onError { TestError() }
                .withIntrospection(performanceModule)
                .build()
                .execute()

        performanceModule.metrics[START_METRIC] shouldBe PERFORMANCE_METRIC
        performanceModule.metrics[END_METRIC] shouldBe PERFORMANCE_METRIC
    }

    "should call the performance module during erroneous execution" {
        val performanceModule = TestPerformanceModule()

        UseCase.Execute<TestError, TestResponse> { throw IOException("Catastrophic fail") }
                .onError { TestError() }
                .withIntrospection(performanceModule)
                .build()
                .execute()

        performanceModule.metrics[START_METRIC] shouldBe PERFORMANCE_METRIC
        performanceModule.metrics[END_METRIC] shouldBe PERFORMANCE_METRIC
    }

    "should return a deferred execution when deferring" {
        val useCase = UseCase.Execute<TestError, TestResponse> { TestResponse() }
                .onError { TestError() }.build()

        useCase.defer().shouldBeInstanceOf<IO<Either<TestError, TestResponse>>>()
    }
}) {

    class TestPerformanceModule : PerformanceModule {
        val metrics = mutableMapOf<String, String>()

        override val name: String
            get() = PERFORMANCE_METRIC

        override fun start(traceId: String) {
            metrics.put(START_METRIC, traceId)
        }

        override fun end(traceId: String) {
            metrics.put(END_METRIC, traceId)
        }
    }

    companion object {
        const val PERFORMANCE_METRIC = "Test"
        const val START_METRIC = "start"
        const val END_METRIC = "end"
    }
}
