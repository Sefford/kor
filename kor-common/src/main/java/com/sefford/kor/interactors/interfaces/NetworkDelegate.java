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

import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.responses.ResponseInterface;

/**
 * Interface for performing interactions to the network.
 * <p/>
 * A delegate is intended to go through at least four phases
 * <p/>
 * <ul>
 * <li>Network Retrieval phase.</li>
 * <li>Post processing.</li>
 * <li>Saving to cache.</li>
 * <li></li>
 * <p/>
 * </ul>
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface NetworkDelegate<R extends ResponseInterface, E extends ErrorInterface> extends InteractorIdentification {

    /**
     * Produces a {@link com.sefford.kor.responses.ResponseInterface ResponseInterface} type.
     * <p/>
     * In this phase the intended behavior will be to fetch data from the network and parse it into
     * a manageable object by the developer method of choice.
     * <p/>
     * The completion of this method should indicate that the network response was correctly fetched
     * and parsed. If there is any error, an exception of the appropiate type should be thrown and
     * catched.
     *
     * @return A Response from the delegate Type
     */
    R retrieveNetworkResponse() throws Exception;

    /**
     * Does a processing of the Response.
     * <p/>
     * In the default implementation this is a chance for the developer to execute code regarding the response.
     * This might range from crossing information from the cache to the fetched data to validate it.
     * <p/>
     * While it is not an expected behavior, the developer can still have a chance to throw an exception.
     *
     * @param content Response content to Process
     * @return A modified Response content
     */
    R postProcess(R content);

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
     * @param object Response content to save to Cache
     */
    void saveToCache(R object);

    /**
     * Generates a BaseError from an exception.
     * <p/>
     * Gives the developer to extend some information to the error before notifying the UI that the
     * delegate failed.
     *
     * @param error Exception that generated the error.
     * @return Composed Error extending {@link com.sefford.kor.errors.ErrorInterface ErrorInterface}
     */
    E composeErrorResponse(Exception error);
}
