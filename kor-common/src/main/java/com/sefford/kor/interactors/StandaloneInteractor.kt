/*
 * Copyright (C) 2015 Saúl Díaz
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
package com.sefford.kor.interactors


import com.sefford.common.NullPostable
import com.sefford.common.interfaces.Loggable
import com.sefford.common.interfaces.Postable

import java.util.concurrent.ThreadPoolExecutor

/**
 * This is the Standalone interactor. Decorates one of the simple Interactor strategies.
 *
 *
 * It is meant to supersede old [Executor][com.sefford.kor.providers.interfaces.Executor] in basic situations
 * by providing an Interactor that can function on its own. It will help on reducing constructor signatures on modules
 * that require a small number of use cases.
 *
 *
 * For more fine-grained thread management, Executor implementations are advised.
 *
 * @author Saul Diaz Gonzalez<sefford></sefford>@gmail.com>
 */
abstract class StandaloneInteractor<O : Any>
/**
 * Creates a new instance of the Standalone Interactor.
 *
 * @param log      Logging facilities
 * @param executor Execution element
 */
protected constructor(
        /**
         * Logging facilities for the interactor
         */
        protected val log: Loggable,
        /**
         * Execution element for the Interactor
         */
        internal val executor: ThreadPoolExecutor) {

    /**
     * Instantiates the new [Interactor] that will be executed on [execute][.execute]
     * method.
     *
     * @param bus    Postable where the [responses][com.sefford.kor.responses.Response] and the [errors][com.sefford.kor.errors.Error] will come through
     * @param params Extension element to help the Interactor to configure itself
     * @return Interactor instance to be executed
     */
    protected abstract fun instantiateInteractor(bus: Postable, params: O?): Interactor<*, *>

    /**
     * Executes the Interactor
     *
     * @param bus     Postable where the [responses][com.sefford.kor.responses.Response] and the [errors][com.sefford.kor.errors.Error] will come through
     * @param options Extension element to help the Interactor to configure itself
     */
    fun execute(bus: Postable, options: O?) {
        executor.execute(instantiateInteractor(bus, options))
    }

    /**
     * Executes the Interactor syncronously
     *
     * @param params
     * @return Extension element to help the Interactor to configure itself
     */
    fun execute(params: O): Any {
        return instantiateInteractor(NullPostable.INSTANCE, params).execute()
    }
}
