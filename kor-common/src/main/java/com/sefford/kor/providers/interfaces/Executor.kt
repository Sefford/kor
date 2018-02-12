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
package com.sefford.kor.providers.interfaces

/**
 * Basic provider interface to unify the Provider APIs.
 *
 *
 * In general terms a Executor intends to be an API to execute and managing an interactor queue. It has
 * access to very basic API as not all the ways of providing a delegate queue allow for an extensive
 * manipulation.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface Executor<T> {


    /**
     * Executes a Single operation, determining if the Interactor requires to bring the content from the Network or the
     * Cache
     *
     * @param interactor Request to process
     */
    fun executeOperation(interactor: T)

    /**
     * Executes multiple operations.
     *
     * @param interactors List of Interactors to process
     */
    fun executeOperations(interactors: List<T>)

    /**
     * Clears the queue.
     */
    fun clear()
}
