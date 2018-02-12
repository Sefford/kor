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

package com.sefford.kor.responses

/**
 * Interface that declares basic information for managing a response
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface Response {
    /**
     * Returns if the Interactor was successful.
     *
     *
     * This flag indicates if the delegate was or not successful independently of the result of the
     * network operation. While typically a successful delegate completes, there could be cases
     * where the delegate actually is not successful because of the data.
     *
     * @return TRUE if it was, FALSE otherwise
     */
    val success: Boolean

    /**
     * Returns if the delegate comes from the network or not.
     *
     *
     * The basic implementation only discerns between network and non-network (lru), but extensions of
     * ResponseInterface may allow for a more fine-grained information about their sources.
     *
     * @return TRUE if the response comes from the network, FALSE othewise
     */
    val fromNetwork: Boolean
}
