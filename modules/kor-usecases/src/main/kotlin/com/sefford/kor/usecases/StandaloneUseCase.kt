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
import com.sefford.common.interfaces.Postable
import com.sefford.kor.usecases.components.Error
import com.sefford.kor.usecases.components.Response
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Standalone use case that allows individual execution of a Single use case
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface StandaloneUseCase<P, E : Error, R : Response> {

    /**
     * Instantiates the internal use case for execution.
     *
     * This is executed ad-hoc when any of the execution methods are invoked, so
     * the use case is instantiated and it does not retain state.
     *
     * @param params Parameter configuration of the use case
     */
    fun instantiate(params: P): UseCase<E, R>

    /**
     * Executes the use case syncronously.
     *
     * @param params Parameter configuration of the use case
     */
    fun execute(params: P): Either<E, R> = instantiate(params).execute()

    /**
     * Executes the use case synchronously and outputs the
     * results via a {@link Postable Postable} element.
     *
     * @param postable Postable element where to output the results
     * @param params Parameter configuration of the use case
     *
     */
    fun execute(postable: Postable, params: P) = execute(params).fold({ postable.post(it) }, { postable.post(it) })

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute it.
     *
     * @param params Parameter configuration of the use case
     */
    fun defer(params: P) = defer(Dispatchers.IO, params)

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute it.
     *
     * @param thread Execution context of the use case. Defaults to {@link BackgroundPool BackgroundPool}
     * @param params Parameter configuration of the use case
     */
    fun defer(thread: CoroutineContext = Dispatchers.IO, params: P) =
            IO(thread) { execute(params) }

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute it.
     *
     * @param thread Execution context of the use case. Defaults to {@link BackgroundPool BackgroundPool}
     * @param postable postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    fun defer(thread: CoroutineContext = Dispatchers.IO, postable: Postable, params: P) =
            IO(thread) { execute(postable, params) }

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute or combine
     * it on a functional algebra
     *
     * @param params Parameter configuration of the use case
     */
    @Deprecated("Use defer instead", replaceWith = ReplaceWith("defer(params)"))
    fun asynk(params: P) = defer(params)

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute or combine
     * it on a functional algebra
     *
     * @param thread Execution context of the use case. Defaults to {@link BackgroundPool BackgroundPool}
     * @param params Parameter configuration of the use case
     */
    @Deprecated("Use defer instead", replaceWith = ReplaceWith("defer(thread, params)"))
    fun asynk(thread: CoroutineContext = Dispatchers.IO, params: P) = defer(thread, params)

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute or combine
     * it on a functional algebra
     *
     * @param postable postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    @Deprecated("Use defer instead", replaceWith = ReplaceWith("defer(thread, postable, params)"))
    fun asynk(postable: Postable, params: P) = defer(Dispatchers.IO, postable, params)

    /**
     * Obtains the instance of the execution and returns it in the given thread, in order to lazily execute or combine
     * it on a functional algebra
     *
     * @param thread Execution context of the use case. Defaults to {@link BackgroundPool BackgroundPool}
     * @param postable postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    @Deprecated("Use defer instead", replaceWith = ReplaceWith("defer(thread, postable, params)"))
    fun asynk(thread: CoroutineContext = Dispatchers.IO, postable: Postable, params: P) =
            defer(Dispatchers.IO, postable, params)

    /**
     * Executes the use case on a custom coroutine context and outputs the
     * results.
     *
     * @param params Parameter configuration of the use case
     */
    fun async(params: P) = async(Dispatchers.IO, params) { }

    /**
     * Executes the use case on a custom coroutine context and outputs the
     * results.
     *
     * @param thread Execution context of the use case
     * @param params Parameter configuration of the use case
     */
    fun async(thread: CoroutineContext = Dispatchers.IO,
              params: P,
              callback: (Either<Throwable, Either<E, R>>) -> Unit) = defer(thread, params).unsafeRunAsync(callback)

    /**
     * Executes the use case the default asynchronous context {@see BackgroundPool} and outputs the
     * results via a {@link Postable Postable} element.
     *
     * @param Postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    fun async(postable: Postable, params: P) = async(Dispatchers.IO, postable, params)


    /**
     * Executes the use case the default asynchronous context {@see BackgroundPool} and outputs the
     * results via a {@link Postable Postable} element.
     *
     * @param thread Execution context of the use case. Defaults to {@link BackgroundPool BackgroundPool}
     * @param Postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    fun async(thread: CoroutineContext = Dispatchers.IO, postable: Postable, params: P) =
            defer(thread, postable, params).unsafeRunAsync { }

}
