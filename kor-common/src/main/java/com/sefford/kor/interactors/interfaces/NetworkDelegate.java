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
package com.sefford.kor.interactors.interfaces;

import com.sefford.kor.errors.Error;
import com.sefford.kor.responses.Response;

/**
 * Interface for performing interactions to the network.
 * <p/>
 * A delegate is intended to go through at least four phases
 * <p/>
 * <ul>
 * <li>Network Retrieval (execution) phase.</li>
 * <li>Post processing.</li>
 * <li>Saving to lru.</li>
 * <li></li>
 * <p/>
 * </ul>
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface NetworkDelegate<R extends Response, E extends Error> extends Delegate<R, E> {

    /**
     * Does a processing of the Response.
     * <p/>
     * In the default implementation this is a chance for the developer to execute code regarding the response.
     * This might range from crossing information from the lru to the fetched data to validate it.
     * <p/>
     * While it is not an expected behavior, the developer can still have a chance to throw an exception.
     *
     * @param response Response response to Process
     * @return A modified Response response
     */
    R postProcess(R response);

    /**
     * Saves the response to Cache.
     * <p/>
     * This phase will save the data into the repositories according to the structure and architecture of
     * them. Depending on the implementation of the repositories this operation might take long times
     * and provide a bad UX to the user unless the notification actually happens before this method.
     * <p/>
     * For those requests, {@link FastDelegate FastSaving} interface is
     * available for saving to the repository on a fast way, notifying and finally performing a full save.
     *
     * @param response Response content to save to Cache
     */
    void saveToCache(R response);

}
