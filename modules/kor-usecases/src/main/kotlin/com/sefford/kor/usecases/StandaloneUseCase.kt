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
import com.sefford.common.interfaces.Postable
import com.sefford.kor.usecases.components.BackgroundPool
import com.sefford.kor.usecases.components.Error
import com.sefford.kor.usecases.components.Response
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Standalone use case that allows individual execution of a Single use case
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface StandaloneUseCase<P : Any, E : Error, R : Response> {

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
     * Executes the use case depending on a coroutine context and outputs the
     * results via a {@link Postable Postable} element.
     *
     * @param thread Execution context of the use case
     * @postable Postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    fun execute(thread: CoroutineContext = BackgroundPool, postable: Postable, params: P) = launch(thread) { execute(params).fold({ postable.post(it) }, { postable.post(it) }) }

    /**
     * Executes the use case depending on an asynchoronous context context and outputs the
     * results via a {@link Postable Postable} element.
     *
     * @postable Postable element where to output the results
     * @param params Parameter configuration of the use case
     */
    fun async(postable: Postable, params: P) = execute(BackgroundPool, postable, params)

    /**
     * Executes the use case depending on a coroutine context and outputs the
     * results.
     *
     * @param thread Execution context of the use case
     * @param params Parameter configuration of the use case
     */
    suspend fun async(params: P): Either<E, R> = kotlinx.coroutines.experimental.async(BackgroundPool) { instantiate(params).execute() }.await()

}