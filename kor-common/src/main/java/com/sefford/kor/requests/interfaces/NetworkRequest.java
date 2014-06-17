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
package com.sefford.kor.requests.interfaces;

import com.sefford.kor.errors.BaseError;
import com.sefford.kor.responses.BaseResponse;

/**
 * Interface for network requesting
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface NetworkRequest<R extends BaseResponse, E extends BaseError> extends RequestIdentification {

    /**
     * Processes a JSon response coming from the Network. It is a Template Method to take into
     * account if the information has to be cached or not.
     *
     * @return A Response from the request Type
     */
    R retrieveNetworkResponse();

    /**
     * Do a Postprocess of the Network Response.
     *
     * @param content Response content to Process
     * @return A modified Response content
     */
    R postProcess(R content);

    /**
     * Saves the response to Cache
     *
     * @param object Content to save to Cache
     */
    void saveToCache(R object);

    /**
     * Generates a BaseError from an exception
     *
     * @param error Exception error
     * @return Composed Error
     */
    E composeErrorResponse(Exception error);
}
