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

import com.sefford.kor.usecases.components.Error
import com.sefford.kor.usecases.components.Response

/**
 * Updateable Delegate is a delegate which adds a looping facility to a delegate
 *
 * @author Saúl Díaz <sefford@gmail.com>
 */
interface UpdateableDelegate<R : Response, E : Error> : NetworkDelegate<R, E> {

    /**
     * Signals the Updateable Interactor if he requires looping no longer.
     *
     * @return TRUE if the execution has to continue, FALSE otherwise
     */
    fun keepLooping(): Boolean
}