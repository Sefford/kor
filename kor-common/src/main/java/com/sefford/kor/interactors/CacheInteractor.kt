/*
 * Copyright (C) 2014 Saúl Díaz
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

import com.sefford.common.interfaces.Loggable
import com.sefford.common.interfaces.Postable
import com.sefford.kor.errors.Error
import com.sefford.kor.interactors.interfaces.CacheDelegate
import com.sefford.kor.responses.Response

/**
 * CacheInteractor is the default implementation of a strategy for Cache-related Interactors.
 *
 *
 * This strategy retrieves the delegate's information from the Repository then notifies if the information
 * was successfully retrieved from it.
 *
 *
 * To do not disturb the process of fetching from network, as the lru delegate work faster this
 * implementation of CacheInteractor does not notifies of errors to the UI.
 *
 *
 * It delegates the work to a [CacheDelegate][com.sefford.kor.interactors.interfaces.CacheDelegate] object.
 *
 * @author Saul Diaz <sefford></sefford>@gmail.com>
 */
class CacheInteractor<R : Response, E : Error>
/**
 * Creates a new Cache Interactor.
 *
 * @param postable Bus to notify the results
 * @param log      Logging facilities
 */
(
        /**
         * Bus instance to notify the UI the process finished.
         *
         *
         * This could be also done with Handlers, however the Fragments might exist in an inconsistent
         * state.
         */
        postable: Postable,
        /**
         * Logging facilities.
         */
        log: Loggable,
        /**
         * Delegated use case from {@link CacheDelegate CacheDelegate}
         */
        delegate: CacheDelegate<R, E>) : Interactor<R, E>(postable, log, delegate) {

    override fun execute(): Any {
        val start = System.currentTimeMillis()
        try {
            delegate.startPerformanceLog(log)
            val response = delegate.execute()
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
            return response
        } catch (x: Exception) {
            val error = delegate.composeErrorResponse(x)
            if (loggable && error.loggable) {
                delegate.logErrorResponse(log, x)
            }
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
            return error
        }

    }

    companion object {
        /**
         * Cache Interactor Logging tag
         */
        protected val TAG = "CacheInteractor"
    }
}
