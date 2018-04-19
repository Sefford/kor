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
package com.sefford.kor.interactors.delegates

import com.sefford.kor.usecases.components.Response
import com.sefford.kor.usecases.components.Error

/**
 * Interface for performing interactions to the network.
 *
 *
 * A delegate is intended to go through at least four phases
 *
 *
 *
 *  * Network Retrieval (execution) phase.
 *  * Post processing.
 *  * Saving to lru.
 *  *
 *
 *
 *
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface NetworkDelegate<R : Response, E : Error> : Delegate<R, E> {

    /**
     * Does a processing of the Response.
     *
     *
     * In the default implementation this is a chance for the developer to execute code regarding the response.
     * This might range from crossing information from the lru to the fetched data to validate it.
     *
     *
     * While it is not an expected behavior, the developer can still have a chance to throw an exception.
     *
     * @param response Response response to Process
     * @return A modified Response response
     */
    fun postProcess(response: R): R

    /**
     * Saves the response to Cache.
     *
     *
     * This phase will save the data into the repositories according to the structure and architecture of
     * them. Depending on the implementation of the repositories this operation might take long times
     * and provide a bad UX to the user unless the notification actually happens before this method.
     *
     *
     *
     * @param response Response content to save to Cache
     */
    fun saveToCache(response: R)

}
