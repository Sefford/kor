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
package com.sefford.kor.interactors.interfaces

import com.sefford.common.interfaces.Loggable
import com.sefford.kor.responses.Response

/**
 * Base Delegate form with the facilities to provide responses and errors
 *
 * @author Saul Diaz Gonzalez <sefford@gmail.com>
 */
interface Delegate<R : Response, E : com.sefford.kor.errors.Error> {

    /**
     * Returns delegate name.
     *
     *
     * Depending on the information held by the Request object. This method can return a customized
     * name with custom parameters to improve logging information.
     *
     * @return String with delegate name.
     */
    val name: String

    /**
     * Produces a [ResponseInterface][Response] type.
     *
     *
     * In this phase the intended behavior will be to perform some action or logic (fetching network or lru information,
     * sorting...) and parse it into a manageable object by the developer method of choice.
     *
     *
     * The completion of this method should indicate that the network response was correctly fetched
     * and parsed. If there is any error, an exception of the appropiate type should be thrown and
     * catched.
     *
     * @return A Response from the delegate Type
     */
    @Throws(Exception::class)
    fun execute(): R

    /**
     * Generates a BaseError from an exception.
     *
     *
     * Gives the developer to extend some information to the error before notifying the UI that the
     * delegate failed.
     *
     * @param error Exception that generated the error.
     * @return Composed Error extending [ErrorInterface][com.sefford.kor.errors.Error]
     */
    fun composeErrorResponse(error: Exception): E

    /**
     * Logs an error response
     *
     * @param log Loggable element
     */
    fun logErrorResponse(log: Loggable, x: Exception)

    /**
     * Helper to start performance logging
     *
     * @param loggable Loggable interface to access logging facilities
     */
    fun startPerformanceLog(loggable: Loggable)

    /**
     * Helper to finish performance logging
     *
     * @param loggable Loggable interface to access logging facilities
     * @param start Start time when the logging started
     */
    fun endPerformanceLog(loggable: Loggable, start: Long)
}
