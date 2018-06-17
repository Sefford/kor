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

import arrow.core.*
import com.sefford.kor.usecases.components.*

/**
 * Use case implementation.
 *
 * As stated in the documentation, it passes through three stages
 * <ul>
 * <li>Execution</li>
 * <li>Postprocessing</li>
 * <li>Persistance</li>
 * </ul>
 *
 * By default the postprocessing and persistance stages are optional and
 * are defaulted to an identity lambda.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class UseCase<E : Error, R : Response>
/**
 * Creates a new use case.
 * @param logic Lambda that outputs a {@link Response Response}
 * @param postProcessor Lambda that processes the response from the logic phase and returns it.
 *
 * Note that it can be passed back or create a new one. By default it is an identity lambda.
 * @param cachePersistance Lambda that caches the response from the postprocessing phase and returns it.
 *
 * Note that it can be passed back or create a new one. This is the final result that will be returned by the use case.
 *
 * By default it is an identity lambda.
 */
private constructor(internal val logic: () -> R,
                    internal val postProcessor: (response: R) -> R = ::identity,
                    internal val cachePersistance: (response: R) -> R = ::identity,
                    internal val errorHandler: (ex: Throwable) -> E,
                    internal val performance: PerformanceModule = NoModule) {

    /**
     * Execute the use case inmediately in the current thread.
     */
    fun execute(): Either<E, R> {
        performance.start()
        return Try { cachePersistance(postProcessor(logic())) }
                .also { performance.end() }
                .map { response -> response.right() }
                .getOrElse { errorHandler(it).left() }
    }

    /**
     * Use case builder
     *
     * @author Saul Diaz <sefford@gmail.com>
     */
    class Execute<E : Error, R : Response>
    /**
     * Initializes a new use case with the required logic.
     *
     * If no further piece is returned, postprocessor and persistance phases are an identity function and the
     * error handling is a default one to produce a basic error.
     *
     * @param logic Logic of the use case.
     */
    (internal val logic: () -> R) {
        internal var postProcessor: (R) -> R = ::identity
        internal var cachePersistance: (R) -> R = ::identity
        internal var errorHandler: (Throwable) -> E = emptyErrorHandler()
        internal var performanceModule: PerformanceModule = NoModule

        /**
         * Sets up the logic for the post processing phase.
         *
         * The response returned by this piece of logic will be fed to the persistence phase, and does not matter
         * if the input is returned or a new response is created.
         *
         * @param postProcessor Logic that will be applied to the post processor phase
         */
        fun process(postProcessor: (R) -> R): Execute<E, R> {
            this.postProcessor = postProcessor
            return this
        }

        /**
         * Sets up the logic for the persistence phase.
         *
         * The response returned by this piece of logic will be returned, and does not matter if the input is returned
         * or a new response is created.
         *
         * @param cachePersistance Logic that  will be applied to the persistance phase.
         */
        fun persist(cachePersistance: (R) -> R): Execute<E, R> {
            this.cachePersistance = cachePersistance
            return this
        }

        /**
         * Sets up a customized error handling.
         *
         * This will be returned in the case of any failure in any of execution, postprocessing or persistance stages.
         *
         * @param errorHandler Custom error handler.
         */
        fun onError(errorHandler: (Throwable) -> E): Execute<E, R> {
            this.errorHandler = errorHandler
            return this;
        }

        /**
         * Sets up a performance module.
         *
         * @param module Performance module which will output the metric
         */
        fun withIntrospection(module: PerformanceModule): Execute<E, R> {
            this.performanceModule = module
            return this;
        }

        /**
         * Builds a new use case as specified
         */
        fun build(): UseCase<E, R> {
            return UseCase(logic, postProcessor, cachePersistance, errorHandler, performanceModule)
        }
    }
}
